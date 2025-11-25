// Configurações da API
const API_BASE_URL = 'http://localhost:8080';

// Elementos do DOM
const loginSection = document.getElementById('loginSection');
const mainContent = document.getElementById('mainContent');
const loginForm = document.getElementById('loginForm');
const loginError = document.getElementById('loginError');
const loginBtnText = document.getElementById('loginBtnText');
const loginLoading = document.getElementById('loginLoading');
const currentUser = document.getElementById('currentUser');
const userEmail = document.getElementById('userEmail');

const tabelaCorpo = document.getElementById('tabela-corpo');
const tabelaCabecalho = document.getElementById('tabela-cabecalho');
const tituloTabela = document.getElementById('titulo-tabela');
const loading = document.getElementById('loading');
const mensagemErro = document.getElementById('mensagem-erro');
const mensagemSucesso = document.getElementById('mensagem-sucesso');
const contadorRegistros = document.getElementById('contador-registros');

// Variáveis globais
let entidadeAtual = '';
let dadosCarregados = [];
let currentToken = localStorage.getItem('jwtToken');
let currentUserData = JSON.parse(localStorage.getItem('userData') || '{}');

// ========== SISTEMA DE AUTENTICAÇÃO ==========

// Verificar se usuário já está logado ao carregar a página
document.addEventListener('DOMContentLoaded', function() {
    if (currentToken && isTokenValid(currentToken)) {
        showMainContent();
    } else {
        showLoginSection();
        clearAuthData();
    }
});

// Função de login
loginForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    await login(username, password);
});

async function login(username, password) {
    try {
        showLoginLoading(true);
        hideLoginError();

        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            // Login bem-sucedido
            currentToken = data.token;
            currentUserData = {
                username: data.username,
                userId: data.userId
            };
            
            // Salvar no localStorage
            localStorage.setItem('jwtToken', currentToken);
            localStorage.setItem('userData', JSON.stringify(currentUserData));
            
            showMainContent();
            showSuccess('Login realizado com sucesso!');
        } else {
            // Erro no login
            throw new Error(data.message || 'Erro no login');
        }
    } catch (error) {
        showLoginError(error.message);
    } finally {
        showLoginLoading(false);
    }
}

function logout() {
    if (confirm('Deseja realmente sair?')) {
        clearAuthData();
        showLoginSection();
        showSuccess('Logout realizado com sucesso!');
    }
}

function clearAuthData() {
    currentToken = null;
    currentUserData = {};
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userData');
    loginForm.reset();
}

function showLoginSection() {
    loginSection.style.display = 'block';
    mainContent.style.display = 'none';
}

function showMainContent() {
    loginSection.style.display = 'none';
    mainContent.style.display = 'block';
    
    // Atualizar informações do usuário
    currentUser.textContent = currentUserData.username || 'Usuário';
    userEmail.textContent = currentUserData.username || '';
    
    // Carregar dados iniciais
    carregarAlunos();
}

function showLoginLoading(show) {
    loginBtnText.style.display = show ? 'none' : 'inline';
    loginLoading.style.display = show ? 'inline-block' : 'none';
}

function showLoginError(message) {
    loginError.textContent = message;
    loginError.style.display = 'block';
}

function hideLoginError() {
    loginError.style.display = 'none';
}

function isTokenValid(token) {
    if (!token) return false;
    
    try {
        // Decodificar token JWT (apenas para verificar expiração)
        const payload = JSON.parse(atob(token.split('.')[1]));
        const expiration = payload.exp * 1000; // Converter para milissegundos
        return Date.now() < expiration;
    } catch (error) {
        return false;
    }
}

function showRegisterForm() {
    alert('Para se cadastrar, use o endpoint POST /user com os dados: username, password, email');
}

// ========== FUNÇÕES GERAIS DA API (ATUALIZADAS COM JWT) ==========

async function fetchAPI(endpoint) {
    try {
        mostrarLoading(true);
        esconderMensagens();

        const headers = {
            'Content-Type': 'application/json'
        };

        // Adicionar token JWT se disponível
        if (currentToken) {
            headers['Authorization'] = `Bearer ${currentToken}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'GET',
            headers: headers
        });
        
        if (response.status === 401) {
            // Token inválido ou expirado
            clearAuthData();
            showLoginSection();
            throw new Error('Sessão expirada. Faça login novamente.');
        }
        
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        
        const data = await response.json();
        return data;
    } catch (error) {
        if (error.message.includes('Sessão expirada')) {
            mostrarErro(error.message);
        } else {
            mostrarErro(`Falha ao carregar dados: ${error.message}`);
        }
        console.error('Erro na requisição:', error);
        return null;
    } finally {
        mostrarLoading(false);
    }
}

async function postAPI(endpoint, data) {
    try {
        mostrarLoading(true);
        esconderMensagens();

        const headers = {
            'Content-Type': 'application/json'
        };

        // Adicionar token JWT se disponível (exceto para criar usuário)
        if (currentToken && !endpoint.includes('/user')) {
            headers['Authorization'] = `Bearer ${currentToken}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data)
        });
        
        if (response.status === 401) {
            clearAuthData();
            showLoginSection();
            throw new Error('Sessão expirada. Faça login novamente.');
        }
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Erro ${response.status}: ${errorText}`);
        }
        
        return await response.json();
    } catch (error) {
        if (error.message.includes('Sessão expirada')) {
            mostrarErro(error.message);
        } else {
            mostrarErro(`Falha ao salvar: ${error.message}`);
        }
        return null;
    } finally {
        mostrarLoading(false);
    }
}

async function putAPI(endpoint, data) {
    try {
        mostrarLoading(true);
        esconderMensagens();

        const headers = {
            'Content-Type': 'application/json'
        };

        if (currentToken) {
            headers['Authorization'] = `Bearer ${currentToken}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'PUT',
            headers: headers,
            body: JSON.stringify(data)
        });
        
        if (response.status === 401) {
            clearAuthData();
            showLoginSection();
            throw new Error('Sessão expirada. Faça login novamente.');
        }
        
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        
        return true;
    } catch (error) {
        if (error.message.includes('Sessão expirada')) {
            mostrarErro(error.message);
        } else {
            mostrarErro(`Falha ao atualizar: ${error.message}`);
        }
        return false;
    } finally {
        mostrarLoading(false);
    }
}

async function deleteAPI(endpoint) {
    try {
        mostrarLoading(true);
        esconderMensagens();

        const headers = {};

        if (currentToken) {
            headers['Authorization'] = `Bearer ${currentToken}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE',
            headers: headers
        });
        
        if (response.status === 401) {
            clearAuthData();
            showLoginSection();
            throw new Error('Sessão expirada. Faça login novamente.');
        }
        
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        
        return true;
    } catch (error) {
        if (error.message.includes('Sessão expirada')) {
            mostrarErro(error.message);
        } else {
            mostrarErro(`Falha ao excluir: ${error.message}`);
        }
        return false;
    } finally {
        mostrarLoading(false);
    }
}

// ========== FUNÇÕES DE CARREGAMENTO ==========

async function carregarAlunos() {
    if (!currentToken) {
        mostrarErro('Faça login para acessar os dados');
        return;
    }

    entidadeAtual = 'aluno';
    tituloTabela.textContent = 'Lista de Alunos';
    const alunos = await fetchAPI('/aluno');
    
    if (alunos) {
        dadosCarregados = alunos;
        renderizarTabelaAlunos(alunos);
    }
}

async function carregarProfessores() {
    if (!currentToken) {
        mostrarErro('Faça login para acessar os dados');
        return;
    }

    entidadeAtual = 'professor';
    tituloTabela.textContent = 'Lista de Professores';
    const professores = await fetchAPI('/professor');
    
    if (professores) {
        dadosCarregados = professores;
        renderizarTabelaProfessores(professores);
    }
}

async function carregarDisciplinas() {
    if (!currentToken) {
        mostrarErro('Faça login para acessar os dados');
        return;
    }

    entidadeAtual = 'disciplina';
    tituloTabela.textContent = 'Lista de Disciplinas';
    const disciplinas = await fetchAPI('/disciplina');
    
    if (disciplinas) {
        dadosCarregados = disciplinas;
        renderizarTabelaDisciplinas(disciplinas);
    }
}

async function carregarTurmas() {
    if (!currentToken) {
        mostrarErro('Faça login para acessar os dados');
        return;
    }

    entidadeAtual = 'turma';
    tituloTabela.textContent = 'Lista de Turmas';
    const turmas = await fetchAPI('/turma');
    
    if (turmas) {
        dadosCarregados = turmas;
        renderizarTabelaTurmas(turmas);
    }
}

async function carregarNotas() {
    if (!currentToken) {
        mostrarErro('Faça login para acessar os dados');
        return;
    }

    entidadeAtual = 'nota';
    tituloTabela.textContent = 'Lista de Notas';
    const notas = await fetchAPI('/nota');
    
    if (notas) {
        dadosCarregados = notas;
        renderizarTabelaNotas(notas);
    }
}

async function carregarMatriculas() {
    if (!currentToken) {
        mostrarErro('Faça login para acessar os dados');
        return;
    }

    entidadeAtual = 'matricula';
    tituloTabela.textContent = 'Lista de Matrículas';
    const matriculas = await fetchAPI('/turmaaluno');
    
    if (matriculas) {
        dadosCarregados = matriculas;
        renderizarTabelaMatriculas(matriculas);
    }
}

// ========== FUNÇÕES DE RENDERIZAÇÃO ==========

function renderizarTabelaAlunos(alunos) {
    tabelaCabecalho.innerHTML = `
        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>CPF</th>
            <th>Ações</th>
        </tr>
    `;

    tabelaCorpo.innerHTML = alunos.map(aluno => `
        <tr>
            <td>${aluno.id}</td>
            <td>${aluno.nome}</td>
            <td>${formatarCPF(aluno.cpf)}</td>
            <td>
                <button class="btn btn-sm btn-warning btn-action" onclick="editarAluno(${aluno.id})">Editar</button>
                <button class="btn btn-sm btn-danger btn-action" onclick="excluirAluno(${aluno.id})">Excluir</button>
            </td>
        </tr>
    `).join('');

    atualizarContador(alunos.length);
}

function renderizarTabelaProfessores(professores) {
    tabelaCabecalho.innerHTML = `
        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Email</th>
            <th>Telefone</th>
            <th>Ações</th>
        </tr>
    `;

    tabelaCorpo.innerHTML = professores.map(professor => `
        <tr>
            <td>${professor.id}</td>
            <td>${professor.nome}</td>
            <td>${professor.email}</td>
            <td>${professor.telefone || 'Não informado'}</td>
            <td>
                <button class="btn btn-sm btn-warning btn-action" onclick="editarProfessor(${professor.id})">Editar</button>
                <button class="btn btn-sm btn-danger btn-action" onclick="excluirProfessor(${professor.id})">Excluir</button>
            </td>
        </tr>
    `).join('');

    atualizarContador(professores.length);
}

function renderizarTabelaDisciplinas(disciplinas) {
    tabelaCabecalho.innerHTML = `
        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Carga Horária</th>
            <th>Ementa</th>
            <th>Ações</th>
        </tr>
    `;

    tabelaCorpo.innerHTML = disciplinas.map(disciplina => `
        <tr>
            <td>${disciplina.id}</td>
            <td>${disciplina.nome}</td>
            <td>${disciplina.cargaHoraria}h</td>
            <td>${disciplina.ementa ? disciplina.ementa.substring(0, 50) + '...' : 'Não informada'}</td>
            <td>
                <button class="btn btn-sm btn-warning btn-action" onclick="editarDisciplina(${disciplina.id})">Editar</button>
                <button class="btn btn-sm btn-danger btn-action" onclick="excluirDisciplina(${disciplina.id})">Excluir</button>
            </td>
        </tr>
    `).join('');

    atualizarContador(disciplinas.length);
}

function renderizarTabelaTurmas(turmas) {
    tabelaCabecalho.innerHTML = `
        <tr>
            <th>ID</th>
            <th>Disciplina</th>
            <th>Professor</th>
            <th>Ano</th>
            <th>Período</th>
            <th>Ações</th>
        </tr>
    `;

    tabelaCorpo.innerHTML = turmas.map(turma => `
        <tr>
            <td>${turma.id}</td>
            <td>${turma.disciplina?.nome || 'N/A'}</td>
            <td>${turma.professor?.nome || 'N/A'}</td>
            <td>${turma.ano}</td>
            <td>${turma.periodo}</td>
            <td>
                <button class="btn btn-sm btn-warning btn-action" onclick="editarTurma(${turma.id})">Editar</button>
                <button class="btn btn-sm btn-danger btn-action" onclick="excluirTurma(${turma.id})">Excluir</button>
            </td>
        </tr>
    `).join('');

    atualizarContador(turmas.length);
}

function renderizarTabelaNotas(notas) {
    tabelaCabecalho.innerHTML = `
        <tr>
            <th>ID</th>
            <th>Aluno</th>
            <th>Turma</th>
            <th>Valor</th>
            <th>Observação</th>
            <th>Ações</th>
        </tr>
    `;

    tabelaCorpo.innerHTML = notas.map(nota => `
        <tr>
            <td>${nota.id}</td>
            <td>${nota.aluno?.nome || 'N/A'}</td>
            <td>${nota.turma?.disciplina?.nome || 'N/A'}</td>
            <td>${nota.valor}</td>
            <td>${nota.observacao || 'Sem observação'}</td>
            <td>
                <button class="btn btn-sm btn-warning btn-action" onclick="editarNota(${nota.id})">Editar</button>
                <button class="btn btn-sm btn-danger btn-action" onclick="excluirNota(${nota.id})">Excluir</button>
            </td>
        </tr>
    `).join('');

    atualizarContador(notas.length);
}

function renderizarTabelaMatriculas(matriculas) {
    tabelaCabecalho.innerHTML = `
        <tr>
            <th>ID</th>
            <th>Aluno</th>
            <th>Turma</th>
            <th>Data Ingresso</th>
            <th>Ativo</th>
            <th>Ações</th>
        </tr>
    `;

    tabelaCorpo.innerHTML = matriculas.map(matricula => `
        <tr>
            <td>${matricula.id}</td>
            <td>${matricula.aluno?.nome || 'N/A'}</td>
            <td>${matricula.turma?.disciplina?.nome || 'N/A'}</td>
            <td>${formatarData(matricula.dataIngresso)}</td>
            <td>${matricula.ativo ? '✅' : '❌'}</td>
            <td>
                <button class="btn btn-sm btn-warning btn-action" onclick="editarMatricula(${matricula.id})">Editar</button>
                <button class="btn btn-sm btn-danger btn-action" onclick="excluirMatricula(${matricula.id})">Excluir</button>
            </td>
        </tr>
    `).join('');

    atualizarContador(matriculas.length);
}

// ========== FUNÇÕES CRUD - ALUNOS ==========

function abrirModalAluno(aluno = null) {
    if (!currentToken) {
        mostrarErro('Faça login para cadastrar alunos');
        return;
    }
    alert('Modal Aluno - Em desenvolvimento');
}

async function salvarAluno() {
    if (!currentToken) {
        mostrarErro('Faça login para salvar alunos');
        return;
    }
    // Implementação do salvar aluno
}

async function editarAluno(id) {
    if (!currentToken) {
        mostrarErro('Faça login para editar alunos');
        return;
    }
    const aluno = dadosCarregados.find(a => a.id === id);
    if (aluno) {
        abrirModalAluno(aluno);
    }
}

async function excluirAluno(id) {
    if (!currentToken) {
        mostrarErro('Faça login para excluir alunos');
        return;
    }
    if (!confirm('Tem certeza que deseja excluir este aluno?')) return;

    const success = await deleteAPI(`/aluno/${id}`);
    if (success) {
        mostrarSucesso('Aluno excluído com sucesso!');
        carregarAlunos();
    }
}

// ========== FUNÇÕES CRUD - PROFESSORES ==========

function abrirModalProfessor(professor = null) {
    if (!currentToken) {
        mostrarErro('Faça login para cadastrar professores');
        return;
    }
    alert('Modal Professor - Em desenvolvimento');
}

async function salvarProfessor() {
    if (!currentToken) {
        mostrarErro('Faça login para salvar professores');
        return;
    }
    // Implementação do salvar professor
}

async function editarProfessor(id) {
    if (!currentToken) {
        mostrarErro('Faça login para editar professores');
        return;
    }
    const professor = dadosCarregados.find(p => p.id === id);
    if (professor) {
        abrirModalProfessor(professor);
    }
}

async function excluirProfessor(id) {
    if (!currentToken) {
        mostrarErro('Faça login para excluir professores');
        return;
    }
    if (!confirm('Tem certeza que deseja excluir este professor?')) return;

    const success = await deleteAPI(`/professor/${id}`);
    if (success) {
        mostrarSucesso('Professor excluído com sucesso!');
        carregarProfessores();
    }
}

// ========== FUNÇÕES CRUD - DISCIPLINAS ==========

function abrirModalDisciplina(disciplina = null) {
    if (!currentToken) {
        mostrarErro('Faça login para cadastrar disciplinas');
        return;
    }
    alert('Modal Disciplina - Em desenvolvimento');
}

async function salvarDisciplina() {
    if (!currentToken) {
        mostrarErro('Faça login para salvar disciplinas');
        return;
    }
    // Implementação do salvar disciplina
}

async function editarDisciplina(id) {
    if (!currentToken) {
        mostrarErro('Faça login para editar disciplinas');
        return;
    }
    const disciplina = dadosCarregados.find(d => d.id === id);
    if (disciplina) {
        abrirModalDisciplina(disciplina);
    }
}

async function excluirDisciplina(id) {
    if (!currentToken) {
        mostrarErro('Faça login para excluir disciplinas');
        return;
    }
    if (!confirm('Tem certeza que deseja excluir esta disciplina?')) return;

    const success = await deleteAPI(`/disciplina/${id}`);
    if (success) {
        mostrarSucesso('Disciplina excluída com sucesso!');
        carregarDisciplinas();
    }
}

// ========== FUNÇÕES AUXILIARES ==========

function mostrarLoading(mostrar) {
    loading.style.display = mostrar ? 'block' : 'none';
}

function mostrarErro(mensagem) {
    mensagemErro.textContent = mensagem;
    mensagemErro.style.display = 'block';
}

function mostrarSucesso(mensagem) {
    mensagemSucesso.textContent = mensagem;
    mensagemSucesso.style.display = 'block';
    setTimeout(() => mensagemSucesso.style.display = 'none', 5000);
}

function esconderMensagens() {
    mensagemErro.style.display = 'none';
    mensagemSucesso.style.display = 'none';
}

function atualizarContador(quantidade) {
    contadorRegistros.textContent = `${quantidade} registros`;
}

function formatarCPF(cpf) {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function formatarData(dataString) {
    if (!dataString) return 'N/A';
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR');
}

// Placeholder functions para as outras entidades
function abrirModalTurma() { 
    if (!currentToken) {
        mostrarErro('Faça login para cadastrar turmas');
        return;
    }
    alert('Modal Turma - Em desenvolvimento'); 
}

function abrirModalNota() { 
    if (!currentToken) {
        mostrarErro('Faça login para cadastrar notas');
        return;
    }
    alert('Modal Nota - Em desenvolvimento'); 
}

function abrirModalMatricula() { 
    if (!currentToken) {
        mostrarErro('Faça login para cadastrar matrículas');
        return;
    }
    alert('Modal Matrícula - Em desenvolvimento'); 
}

function editarTurma(id) { 
    if (!currentToken) {
        mostrarErro('Faça login para editar turmas');
        return;
    }
    alert('Editar Turma - Em desenvolvimento'); 
}

function excluirTurma(id) { 
    if (!currentToken) {
        mostrarErro('Faça login para excluir turmas');
        return;
    }
    alert('Excluir Turma - Em desenvolvimento'); 
}

function editarNota(id) { 
    if (!currentToken) {
        mostrarErro('Faça login para editar notas');
        return;
    }
    alert('Editar Nota - Em desenvolvimento'); 
}

function excluirNota(id) { 
    if (!currentToken) {
        mostrarErro('Faça login para excluir notas');
        return;
    }
    alert('Excluir Nota - Em desenvolvimento'); 
}

function editarMatricula(id) { 
    if (!currentToken) {
        mostrarErro('Faça login para editar matrículas');
        return;
    }
    alert('Editar Matrícula - Em desenvolvimento'); 
}

function excluirMatricula(id) { 
    if (!currentToken) {
        mostrarErro('Faça login para excluir matrículas');
        return;
    }
    alert('Excluir Matrícula - Em desenvolvimento'); 
}