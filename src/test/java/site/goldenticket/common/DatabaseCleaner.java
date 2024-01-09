package site.goldenticket.common;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DatabaseCleaner {

    private final List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    private void findDatabaseTableNames() {
        Query showTables = entityManager.createNativeQuery("SHOW TABLES");
        if (Objects.isNull(showTables)) {
            return;
        }

        List<Object[]> tableInfos = showTables.getResultList();
        for (Object[] tableInfo : tableInfos) {
            String tableName = (String) tableInfo[0];
            tableNames.add(tableName);
        }
    }

    private void truncate() {
        entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS %d", 0))
            .executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format("TRUNCATE TABLE %s", tableName))
                .executeUpdate();
        }
        entityManager.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS %d", 1))
            .executeUpdate();
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }
}
