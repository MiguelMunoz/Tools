package com.neptunedreams.heroes.api;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neptunedreams.heroes.data.HeroRepository;
import com.neptunedreams.heroes.entity.HeroDb;
import com.neptunedreams.heroes.model.Hero;
import com.neptunedreams.heroes.model.Id;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import static com.neptunedreams.framework.PojoUtility.*;
import static com.neptunedreams.framework.ResponseUtility.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/7/18
 * <p>Time: 9:49 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Controller
public class HeroesImpl implements HeroesApiDelegate { //}, Serve<Integer, HeroDb> {
	@NonNls
	private static final Logger log = LoggerFactory.getLogger(HeroesImpl.class);
	private HeroRepository heroRepository;
	private ObjectMapper objectMapper;

	@Autowired
	public HeroesImpl(final HeroRepository heroRepository, final ObjectMapper objectMapper) {
		this.heroRepository = heroRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	public ResponseEntity<Id> addHero(final Hero hero) {
		return serveCreated(() -> {
			HeroDb heroDb = objectMapper.convertValue(hero, HeroDb.class);
			HeroDb saved = heroRepository.save(heroDb);
			Id id = new Id();
			id.setId(saved.getId());
			return id;
		});
	}

	@Override
	public ResponseEntity<Void> deleteHero(final Integer id) {
		return serveOK(() -> {
			confirmExists(id, heroRepository.exists(id));
			heroRepository.delete(id);
			return null;
		});
	}

	@Override
	public ResponseEntity<Hero> getHero(final Integer id) {
		return serveOK(() -> {
			HeroDb heroDb = heroRepository.findOne(id);
			confirmFound(heroDb, id);
			final Hero hero = objectMapper.convertValue(heroDb, Hero.class);
			log.info("Returning Hero {} converted from {}", hero, heroDb);
			return hero;
		});
	}

	@Override
	public ResponseEntity<List<Hero>> getHeroes(final String terms) {
		log.info("Terms: {} null={}", terms, terms==null);
		if (terms == null) {
			return getTheHeroes();
		}
		return getTheHeroes(terms);
	}

	private ResponseEntity<List<Hero>> getTheHeroes(final String term) {
		return serveOK(() -> {
			log.info("Searching for {}", term);
			final List<HeroDb> foundHeroes = heroRepository.findLike(term);
			log.info("Search for {} returned {} results", term, foundHeroes.size());
			return objectMapper.convertValue(foundHeroes, new TypeReference<List<Hero>>() {
			});
		});
	}
	
	private ResponseEntity<List<Hero>> getTheHeroes() {
		return serveOK(() -> {
			final List<HeroDb> all = heroRepository.findAll();
			return objectMapper.convertValue(all, new TypeReference<List<Hero>>() { });
		});
	}

	@Override
	public ResponseEntity<Void> heroesPut(final Hero hero) {
		return serveVoidOK(() -> {
			confirmFound(hero, hero.getId());
			heroRepository.save(objectMapper.convertValue(hero, HeroDb.class));
		});
	}
}
