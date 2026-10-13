package gr.cite.rda.data.namingstrategy;

import gr.cite.rda.common.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({NamingStrategyProperties.class})
public class PrefixPhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private final NamingStrategyProperties namingStrategyProperties;

    public PrefixPhysicalNamingStrategy(NamingStrategyProperties namingStrategyProperties) {
        this.namingStrategyProperties = namingStrategyProperties;
    }

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment context) {
        if (StringUtils.isNullOrEmpty(namingStrategyProperties.getPrefix()))
            return super.toPhysicalTableName(logicalName, context);
        Identifier identifier = new Identifier(namingStrategyProperties.getPrefix() + logicalName.getText(), logicalName.isQuoted());
        return super.toPhysicalTableName(identifier, context);
    }

}
