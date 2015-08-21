package com.github.hippoom.springtestdbunittemplate.sample;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class PersistenceTestConfig {

    @Bean
    public ModelMapperFactory modelMapper() {
        return new ModelMapperFactory();
    }

    @Bean
    @Autowired
    public TransactionTemplateFactory transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplateFactory(transactionManager);
    }

    public static class ModelMapperFactory {

        public static ModelMapper newInstance(PropertyMap<?, ?> orderMap) {
            final ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration()
                    .setFieldMatchingEnabled(true)
                    .setFieldAccessLevel(PRIVATE);
            modelMapper.addMappings(orderMap);

            return modelMapper;
        }
    }

    public class TransactionTemplateFactory {
        private PlatformTransactionManager transactionManager;

        public TransactionTemplateFactory(PlatformTransactionManager transactionManager) {
            this.transactionManager = transactionManager;
        }

        public TransactionTemplate newInstance() {
            return new TransactionTemplate(transactionManager);
        }
    }
}
