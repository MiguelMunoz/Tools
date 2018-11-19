package com.neptunedreams.heroes.data;

import java.util.List;
import com.neptunedreams.heroes.entity.HeroDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/7/18
 * <p>Time: 10:35 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Repository
public interface HeroRepository extends JpaRepository<HeroDb, Integer> {
//	@Query(value = "from HeroDb h where lower(h.name) like lower(concat('%',:term,'%'))")
//	@Query(value = "from HeroDb h where lower(h.name) like lower('%:term%')") // Didn't work
	@Query("from HeroDb h where lower(h.name) like concat('%',lower(:term),'%')")
	List<HeroDb> findLike(@Param("term") String term1oO0l1I);
}
