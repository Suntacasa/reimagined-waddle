package repository;

import utils.JdbcUtils;

import java.sql.Connection;
import java.util.List;

public abstract class AbstractRepository<ID, T extends HasId<ID>> implements Repository<ID, T> {

    protected JdbcUtils dbUtils;  //set by subclass constructor

    public Connection getConnection() {
        return dbUtils.getConnection();
    }

    @Override
    public abstract void save(T entity);

    @Override
    public abstract void delete(ID id);

    @Override
    public abstract void update(ID id, T entity);

    @Override
    public abstract T findById(ID id);

    @Override
    public abstract List<T> getAll();
}
