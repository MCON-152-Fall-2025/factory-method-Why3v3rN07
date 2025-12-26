package com.mcon152.recipeshare.web;

import com.mcon152.recipeshare.Recipe;
import com.mcon152.recipeshare.service.RecipeFactory;
import com.mcon152.recipeshare.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Create a new recipe.
     * Returns 201 Created with Location header pointing to the new resource.
     */
    @PostMapping
    public ResponseEntity<Recipe> addRecipe(@RequestBody RecipeRequest recipeRequest) {
        logger.info("Received request: Add recipe");
        try {
            Recipe toSave = RecipeFactory.createFromRequest(recipeRequest);
            logger.debug("Attempting to save recipe {} ({})", toSave.getTitle(), toSave.getRecipeType());
            Recipe saved = recipeService.addRecipe(toSave);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()           // /api/recipes
                    .path("/{id}")                  // /{id}
                    .buildAndExpand(saved.getId())
                    .toUri();
            logger.info("Recipe saved - id={}", saved.getId());
            return ResponseEntity.created(location).body(saved);

        } catch (Exception e) {
            logger.error("Error while saving recipe: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieve all recipes. 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        logger.info("Received request: Get all recipes");
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    /**
     * Retrieve a recipe by id. 200 OK or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable long id) {
        logger.info("Received request: Get recipe by id: {}", id);
        Optional<Recipe> foundRecipe = recipeService.getRecipeById(id);
        if (foundRecipe.isPresent()) {
            logger.info("Recipe found - title={}", foundRecipe.get().getTitle());
            return ResponseEntity.ok(foundRecipe.get());
        } else {
            logger.warn("Recipe not found - id={}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a recipe. 204 No Content if deleted, 404 Not Found otherwise.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        logger.info("Received request: Delete recipe by id: {}", id);
        try {
            boolean deleted = recipeService.deleteRecipe(id);
            if (deleted) {
                logger.info("Recipe deleted - id={}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Recipe not found - id={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error while deleting recipe: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Replace a recipe (full update). 200 OK with updated entity or 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable long id, @RequestBody RecipeRequest updatedRequest) {
        logger.info("Received request: Update recipe by id: {}", id);
        Recipe updatedRecipe = RecipeFactory.createFromRequest(updatedRequest);
        logger.debug("Attempting to update recipe {} ({})", updatedRecipe.getTitle(), updatedRecipe.getRecipeType());
        Optional<Recipe> replacementRecipe = recipeService.updateRecipe(id, updatedRecipe);
        if (replacementRecipe.isPresent()) {
            logger.info("Recipe updated - id={}", replacementRecipe.get().getId());
            return ResponseEntity.ok(replacementRecipe.get());
        } else {
            logger.warn("Recipe not found - id={}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Partial update. 200 OK with updated entity or 404 Not Found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Recipe> patchRecipe(@PathVariable long id, @RequestBody RecipeRequest partialRequest) {
        logger.info("Received request: Patch recipe by id: {}", id);
        Recipe partialRecipe = RecipeFactory.createFromRequest(partialRequest);
        logger.debug("Attempting to patch recipe with {}", partialRecipe);
        Optional<Recipe> patchedRecipe = recipeService.patchRecipe(id, partialRecipe);
        if (patchedRecipe.isPresent()) {
            logger.info("Recipe patched - id={}", patchedRecipe.get().getId());
            return ResponseEntity.ok(patchedRecipe.get());
        } else {
            logger.warn("Recipe not found - id={}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
