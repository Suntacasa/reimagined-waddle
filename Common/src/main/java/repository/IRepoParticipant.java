package repository;

import java.util.List;

public interface IRepoParticipant<ID, E extends HasId<ID>> extends Repository<ID, E> {
    List<E> findByEchipa(String echipa);
}