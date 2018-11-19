package com.neptunedreams.heroes.api;

import java.util.List;
import com.neptunedreams.heroes.model.Hero;
import com.neptunedreams.heroes.model.Id;
import org.springframework.http.ResponseEntity;

/**
 * A delegate to be called by the {@link HeroesApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-17T12:13:56.228Z")

public interface HeroesApiDelegate {

    /**
     * @see HeroesApi#addHero
     */
    ResponseEntity<Id> addHero(Hero hero);

    /**
     * @see HeroesApi#deleteHero
     */
    ResponseEntity<Void> deleteHero(Integer id);

    /**
     * @see HeroesApi#getHero
     */
    ResponseEntity<Hero> getHero(Integer id);

    /**
     * @see HeroesApi#getHeroes
     */
    ResponseEntity<List<Hero>> getHeroes(String term);

    /**
     * @see HeroesApi#heroesPut
     */
    ResponseEntity<Void> heroesPut(Hero hero);

}
