This repository replicates a flyway migration problem we are seeing against postgres. A migration resolver which includes a CREATE INDEX CONCURRENTLY will hang with the index creation command in a wait state.

This uses postgres 9.2 and flyway 3.2.1.

This example uses buildnumber-maven-plugin and sql-maven-plugin to set up a new database for each run. Migrations are then run against this new database.

To run and see the problem:
```bash
$ mvn clean install
```

This will result in the build hanging while trying to apply the CREATE INDEX CONCURRENTLY.

You can then log into postgres (using psql) and see the hung command:
```
test_2015_04_13_110536=# select * from pg_stat_activity;
 datid |        datname         |  pid  | usesysid | usename  | application_name | client_addr | client_hostname | client_port |         backend_start         |          xact_start           |          query_start          |         state_change          | waiting |        state        |                                                                                                   query
-------+------------------------+-------+----------+----------+------------------+-------------+-----------------+-------------+-------------------------------+-------------------------------+-------------------------------+-------------------------------+---------+---------------------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 21095 | test_2015_04_13_110536 | 56695 |    16385 | postgres | psql             |             |                 |          -1 | 2015-04-13 11:10:01.127768-06 | 2015-04-13 11:13:08.936651-06 | 2015-04-13 11:13:08.936651-06 | 2015-04-13 11:13:08.936655-06 | f       | active              | select * from pg_stat_activity;
 21095 | test_2015_04_13_110536 | 56824 |    16385 | postgres |                  | 127.0.0.1   |                 |       52437 | 2015-04-13 11:12:55.438927-06 | 2015-04-13 11:12:55.476442-06 | 2015-04-13 11:12:55.487139-06 | 2015-04-13 11:12:55.487175-06 | f       | idle in transaction | SELECT "version_rank","installed_rank","version","description","type","script","checksum","installed_on","installed_by","execution_time","success" FROM "public"."schema_version" ORDER BY "version_rank"
 21095 | test_2015_04_13_110536 | 56825 |    16385 | postgres |                  | 127.0.0.1   |                 |       52438 | 2015-04-13 11:12:55.443687-06 | 2015-04-13 11:12:55.49024-06  | 2015-04-13 11:12:55.49024-06  | 2015-04-13 11:12:55.490241-06 | t       | active              | CREATE UNIQUE INDEX CONCURRENTLY person_restrict_duplicates_2_idx ON person(name, person_month, person_year)
(3 rows)
```

To achieve a passing build, remove the following from the pom.xml and rerun:
```
<resolvers>
    <resolver>db.migration.V4__ConcurrentUniqueIndexMigrationResolver</resolver>
</resolvers>
```
