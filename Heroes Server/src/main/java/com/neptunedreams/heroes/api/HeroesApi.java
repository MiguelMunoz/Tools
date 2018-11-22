/*
 * NOTE: This class is auto generated by the swagger code generator program (2.3.1).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.neptunedreams.heroes.api;

import java.util.List;
import javax.validation.Valid;
import com.neptunedreams.heroes.model.Hero;
import com.neptunedreams.heroes.model.Id;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-11-16T05:28:22.239Z")

@Api(value = "heroes", description = "the heroes API")
public interface HeroesApi {

    @ApiOperation(value = "Add a new hero", nickname = "addHero", notes = "Add a new hero", response = Id.class, tags = {"developers",})
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "created", response = Id.class),
        @ApiResponse(code = 400, message = "Bad Request")})
    @RequestMapping(value = "/heroes/add",
        produces = {"application/json"},
        consumes = {"application/json"},
        method = RequestMethod.PUT)
    ResponseEntity<Id> addHero(@ApiParam(value = "New Hero", required = true) @Valid @RequestBody Hero hero);


    @ApiOperation(value = "Delete a hero", nickname = "deleteHero", notes = "Delete the hero with the specified id", tags = {"developers",})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    @RequestMapping(value = "/heroes/{id}",
        method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteHero(@ApiParam(value = "id", required = true) @PathVariable("id") Integer id);


    @ApiOperation(value = "Get a hero by id", nickname = "getHero", notes = "Search for a hero by ID. Return 'undefined' if not found ", response = Hero.class, tags = {"developers",})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "ok", response = Hero.class)})
    @RequestMapping(value = "/heroes/{id}",
        produces = {"application/json"},
        method = RequestMethod.GET)
    ResponseEntity<Hero> getHero(@ApiParam(value = "id", required = true) @PathVariable("id") Integer id);


    @ApiOperation(value = "Get array of heroes", nickname = "getHeroes", notes = "Gets heroes with the specified name, or all the heroes if no name is specified. Returns an empty array if name is not found. ", response = Hero.class, responseContainer = "List", tags = {"developers",})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "array of heroes", response = Hero.class, responseContainer = "List"),
        @ApiResponse(code = 404, message = "Not found")})
    @RequestMapping(value = "/heroes",
        produces = {"application/json"},
        method = RequestMethod.GET)
    ResponseEntity<List<Hero>> getHeroes(@ApiParam(value = "A portion of the hero's name") @Valid @RequestParam(value = "term", required = false) String term);


    @ApiOperation(value = "Update or add a hero", nickname = "heroesPut", notes = "Updates an exisiting hero or add a new hero", tags = {"developers",})
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Okay")})
    @RequestMapping(value = "/heroes",
        consumes = {"application/json"},
        method = RequestMethod.PUT)
    ResponseEntity<Void> heroesPut(@ApiParam(value = "Revised Hero", required = true) @Valid @RequestBody Hero hero);

}