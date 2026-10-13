package gr.cite.rda.integrationevent.inbox;

public interface ConsistencyHandler<T extends ConsistencyPredicates> {

    Boolean isConsistent(T consistencyPredicates);

}
