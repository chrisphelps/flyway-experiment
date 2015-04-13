package db.migration;

import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.resolver.MigrationExecutor;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

public class V4__ConcurrentUniqueIndexMigrationResolver implements MigrationResolver {
    @Override
    public Collection<ResolvedMigration> resolveMigrations() {
        return Arrays.asList((ResolvedMigration) new ResolvedMigration() {
            @Override
            public MigrationVersion getVersion() {
                return MigrationVersion.fromVersion("4");
            }

            @Override
            public String getDescription() {
                return "Custom Resolved";
            }

            @Override
            public String getScript() {
                return "db.migration.V4__ConcurrentUniqueIndexMigrationResolver";
            }

            @Override
            public Integer getChecksum() {
                return null;
            }

            @Override
            public MigrationType getType() {
                return MigrationType.CUSTOM;
            }

            @Override
            public String getPhysicalLocation() {
                return null;
            }

            @Override
            public MigrationExecutor getExecutor() {
                return new MigrationExecutor() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        Statement statement = null;
                        try {
                            statement = connection.createStatement();
                            statement.execute("CREATE UNIQUE INDEX CONCURRENTLY person_restrict_duplicates_2_idx ON person(name, person_month, person_year)");
                        } finally {
                            JdbcUtils.closeStatement(statement);
                        }
                    }

                    @Override
                    public boolean executeInTransaction() {
                        return false;
                    }
                };
            }
        });
    }
}
