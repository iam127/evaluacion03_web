package com.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    // Esta configuración habilita el soporte para AOP en Spring
    // El @EnableAspectJAutoProxy permite que Spring detecte los @Aspect
}