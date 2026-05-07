package repository;

public interface IRepoAngajat<ID, E extends HasId<ID>> extends Repository<ID, E> {
    E findByUsername(String username);
}