package repository;

import java.util.List;

public interface Repository<ID,T extends HasId<ID>> {
    void save(T entity);
    void delete(ID id);
    void update(ID id, T entity); //eventual doar dupa T entity
    T findById(ID id);
    List<T> getAll();

}