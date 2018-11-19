package com.neptunedreams.heroes.api;

import java.util.List;
import javax.validation.Valid;
import com.neptunedreams.heroes.model.Hero;
import com.neptunedreams.heroes.model.Id;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-10-17T12:13:56.228Z")

@Controller
public class HeroesApiController implements HeroesApi {

    private final HeroesApiDelegate delegate;

    @org.springframework.beans.factory.annotation.Autowired
    public HeroesApiController(HeroesApiDelegate delegate) {
        this.delegate = delegate;
    }
    public ResponseEntity<Id> addHero(@ApiParam(value = "New Hero" ,required=true )  @Valid @RequestBody Hero hero) {
        return delegate.addHero(hero);
    }

    public ResponseEntity<Void> deleteHero(@ApiParam(value = "id",required=true) @PathVariable("id") Integer id) {
        return delegate.deleteHero(id);
    }

    public ResponseEntity<Hero> getHero(@ApiParam(value = "id",required=true) @PathVariable("id") Integer id) {
        return delegate.getHero(id);
    }

    public ResponseEntity<List<Hero>> getHeroes(
    		@ApiParam(value = "A portion of the hero's name") 
		    @Valid 
		    @RequestParam(value = "term", required = false) 
				    String term) {
        return delegate.getHeroes(term);
    }

    public ResponseEntity<Void> heroesPut(@ApiParam(value = "Revised Hero" ,required=true )  @Valid @RequestBody Hero hero) {
        return delegate.heroesPut(hero);
    }

}
