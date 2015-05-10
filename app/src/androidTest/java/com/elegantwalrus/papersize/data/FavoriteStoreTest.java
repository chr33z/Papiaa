package com.elegantwalrus.papersize.data;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.elegantwalrus.papersize.paper.Paper;

import java.util.List;

/**
 * Created by chris on 09.05.15.
 */
public class FavoriteStoreTest extends AndroidTestCase {

    private FavoriteStore favoriteStore;

    private Paper paper1;
    private Paper paper2;

    private String id_paper_1 = "id1";
    private String id_paper_2 = "id2";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        favoriteStore = new FavoriteStore(context);
        favoriteStore.open();

        paper1 = new Paper("name", "desc", id_paper_1, 0, 0);
        paper2 = new Paper("name", "desc", id_paper_2, 0, 0);
    }

    @Override
    protected void tearDown() throws Exception {
        favoriteStore.close();
        super.tearDown();
    }

    public void testAddingFavorites() throws Exception{
        boolean add_1 = favoriteStore.addFavorite(paper1);
        boolean add_2 = favoriteStore.addFavorite(paper2);

        assertEquals(true, add_1);
        assertEquals(true, add_2);

        boolean paper_1_inList = false;
        boolean paper_2_inList = false;
        List<String> favorites = favoriteStore.getFavorites();
        for (String favorite : favorites) {
            if(favorite.equals(paper1.getId())) {
                paper_1_inList = true;
            } else if(favorite.equals(paper2.getId())) {
                paper_2_inList = true;
            }
        }

        assertTrue(paper_1_inList);
        assertTrue(paper_2_inList);
    }

    public void testDeletingFavorites() throws Exception {
        boolean add_1 = favoriteStore.addFavorite(paper1);
        boolean add_2 = favoriteStore.addFavorite(paper2);

        List<String> favorites = favoriteStore.getFavorites();
        assertTrue(favorites.size() > 0);

        boolean delete_1 = favoriteStore.deleteFavorite(paper1);
        boolean delete_2 = favoriteStore.deleteFavorite(paper2);

        assertEquals(true, delete_1);
        assertEquals(true, delete_2);

        favorites = favoriteStore.getFavorites();
        assertEquals(0, favorites.size());
    }
}
