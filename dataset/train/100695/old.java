public class old{
    protected TypedQuery<Long> getCountQuery(Specification<T> spec) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);

		Root<T> root = applySpecificationToCriteria(spec, query);

		JpaCriteriaQueryContext<Long, T> context = potentiallyAugment(query, root, QueryMode.COUNT);
		query = context.getQuery();
		root = context.getRoot();

		if (query.isDistinct()) {
			query.select(builder.countDistinct(root));
		} else {
			query.select(builder.count(root));
		}
		return em.createQuery(query);
	}
}
