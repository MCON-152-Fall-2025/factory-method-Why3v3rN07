package com.mcon152.recipeshare.service;

import com.mcon152.recipeshare.*;
import com.mcon152.recipeshare.web.RecipeRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeFactoryTest {

    @Test
    void createsBasicByDefault_andCopiesFields() {
        RecipeRequest req = new RecipeRequest();
        req.setTitle("Title");
        req.setDescription("Desc");
        req.setIngredients("I");
        req.setInstructions("N");
        req.setServings(3);

        Recipe r = RecipeFactory.createFromRequest(req);
        assertInstanceOf(BasicRecipe.class, r);
        assertNull(r.getId());
        assertEquals("Title", r.getTitle());
        assertEquals("Desc", r.getDescription());
        assertEquals("I", r.getIngredients());
        assertEquals("N", r.getInstructions());
        assertEquals(Integer.valueOf(3), r.getServings());
    }

    @Test
    void createsSpecifiedSubtypes_caseInsensitive() {
        RecipeRequest req = new RecipeRequest();
        req.setType("VEGETARIAN");
        Recipe r = RecipeFactory.createFromRequest(req);
        assertInstanceOf(VegetarianRecipe.class, r);

        req.setType("DESSERT");
        r = RecipeFactory.createFromRequest(req);
        assertInstanceOf(DessertRecipe.class, r);

        req.setType("DAIRY");
        r = RecipeFactory.createFromRequest(req);
        assertInstanceOf(DairyRecipe.class, r);

        req.setType("SOUP");
        r = RecipeFactory.createFromRequest(req);
        assertInstanceOf(SoupRecipe.class, r);

        req.setType("BASIC");
        r = RecipeFactory.createFromRequest(req);
        assertInstanceOf(BasicRecipe.class, r);

    }

    @Test
    void nullRequest_returnsBasic_withNullFields() {
        Recipe r = RecipeFactory.createFromRequest(null);
        assertInstanceOf(BasicRecipe.class, r);
        assertNull(r.getTitle());
        assertNull(r.getDescription());
        assertNull(r.getIngredients());
        assertNull(r.getInstructions());
        assertNull(r.getServings());
    }

    @Test
    void unknownType_defaultsToBasic() {
        RecipeRequest req = new RecipeRequest();
        req.setType("UNKNOWN");
        Recipe r = RecipeFactory.createFromRequest(req);
        assertInstanceOf(BasicRecipe.class, r);
    }
}

