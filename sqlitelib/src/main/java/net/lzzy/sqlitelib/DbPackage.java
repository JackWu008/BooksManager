package net.lzzy.sqlitelib;


import java.util.List;

public class DbPackage {
    private String databaseName;
    private int version;
    private List<String> createTableSqls;
    private List<String> updateTableSqls;
    private static DbPackage instance;

    private DbPackage(String databaseName, int version, List<String> createSqls, List<String> updateSqls) {
        this.databaseName = databaseName;
        this.version = version;
        this.createTableSqls = createSqls;
        this.updateTableSqls = updateSqls;
    }

    public static DbPackage getInstance(String name, int version, List<String> createSqls, List<String> updateSqls) {//锁定多线程
        if (instance == null) {
            synchronized (DbPackage.class) {
                if (instance == null)
                    instance = new DbPackage(name, version, createSqls, updateSqls);
            }
        }
        return instance;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }


    public static void setInstance(DbPackage instance) {
        DbPackage.instance = instance;
    }

    public List<String> getCreateTableSqls() {
        return createTableSqls;
    }

    public void setCreateTableSqls(List<String> createTableSqls) {
        this.createTableSqls = createTableSqls;
    }

    public List<String> getUpdateTableSqls() {
        return updateTableSqls;
    }

    public void setUpdateTableSqls(List<String> updateTableSqls) {
        this.updateTableSqls = updateTableSqls;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
