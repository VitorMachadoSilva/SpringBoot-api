// package com.vitor.demo.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**") // Aplica a todas as rotas da API
//                 .allowedOrigins(
//                     "http://localhost:5500",    // Live Server do VS Code
//                     "http://127.0.0.1:5500",    // Alternativa do Live Server
//                     "http://localhost:3000",     // Para React (caso use)
//                     "http://localhost:8081"      // Para outros frontends
//                 )
//                 .allowedMethods(
//                     "GET",      // Buscar dados
//                     "POST",     // Criar recursos
//                     "PUT",      // Atualizar recursos
//                     "DELETE",   // Excluir recursos
//                     "OPTIONS"   // Pré-requisição CORS
//                 )
//                 .allowedHeaders("*")             // Permite todos os cabeçalhos
//                 .allowCredentials(true)          // Permite cookies/auth
//                 .maxAge(3600);                   // Cache de pré-flight: 1 hora
//     }
// }