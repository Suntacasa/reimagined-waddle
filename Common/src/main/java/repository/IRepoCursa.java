package repository;

public interface IRepoCursa<ID, E extends HasId<ID>> extends Repository<ID, E> {
    void incrementParticipanti(ID cursaID);
}