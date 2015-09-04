package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.filter.IColumnFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Configuration
public class DbUnitConfig {
    private Map<String, List<String>> pks = new HashMap<String, List<String>>(

    );

    @Bean
    public DatabaseConfigBean config() {

        pks.put("t_gallery_cover", asList("gallery_id", "file_name"));
        pks.put("t_event", asList("id"));

        final DatabaseConfigBean factoryBean = new DatabaseConfigBean();
        factoryBean.setPrimaryKeyFilter(new IColumnFilter() {


            @Override
            public boolean accept(String tableName, Column column) {
                final List<String> strings = pks.get(tableName.toLowerCase());
                if (strings != null) {
                    return strings.contains(column.getColumnName().toLowerCase());
                }
                return false;
            }
        });
        return factoryBean;
    }

    @Bean(name = "dbUnitDatabaseConnection")
    @Autowired
    public DatabaseDataSourceConnection connection(DatabaseConfigBean config, DataSource dataSource) throws Exception {
        final DatabaseDataSourceConnectionFactoryBean factory = new DatabaseDataSourceConnectionFactoryBean();
        factory.setDatabaseConfig(config);
        factory.setDataSource(dataSource);
        return factory.getObject();
    }

}
