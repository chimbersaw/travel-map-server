package ru.chimchima.travelmap.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    private val dataSourceProperties: DataSourceProperties
) {
    @Bean
    fun dataSource(): DataSource = dataSourceProperties
        .initializeDataSourceBuilder()
        .build()
}
