package com.mcon152.recipeshare.service;

import com.mcon152.recipeshare.*;
import com.mcon152.recipeshare.web.RecipeRequest;

public class RecipeFactory {

    public static Recipe createFromRequest(RecipeRequest req) {
        String type = req != null && req.getType() != null ? req.getType().trim().toUpperCase() : "BASIC";

        Recipe out = switch (type) {
            case "VEGETARIAN" -> new VegetarianRecipe();
            case "DESSERT" -> new DessertRecipe();
            case "DAIRY" -> new DairyRecipe();
            case "SOUP" -> new SoupRecipe();
            default -> new BasicRecipe();
        };

        // Ensure new entity and safely copy common fields only if req provided
        out.setId(null);
        if (req != null) {
            out.setTitle(req.getTitle());
            out.setDescription(req.getDescription());
            out.setIngredients(req.getIngredients());
            out.setInstructions(req.getInstructions());
            out.setServings(req.getServings());
            if (out instanceof SoupRecipe && req.getSpiceLevel() != null)  ((SoupRecipe)out).setSpiceLevel(req.getSpiceLevel());
        }

        return out;
    }
}
