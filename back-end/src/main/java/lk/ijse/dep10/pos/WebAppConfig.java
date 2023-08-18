package lk.ijse.dep10.pos;

import lk.ijse.dep10.pos.advice.GlobalExceptionHandler;
import lk.ijse.dep10.pos.api.CustomerController;
import lk.ijse.dep10.pos.api.ItemController;
import lk.ijse.dep10.pos.api.OrderController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ComponentScan
public class WebAppConfig {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        return new MethodValidationPostProcessor();
    }



}
