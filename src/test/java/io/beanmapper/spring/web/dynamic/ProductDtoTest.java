package io.beanmapper.spring.web.dynamic;

import static org.junit.Assert.assertEquals;
import io.beanmapper.spring.model.Artist;
import io.beanmapper.spring.model.ArtistDto;
import io.beanmapper.spring.model.Asset;
import io.beanmapper.spring.model.Organization;
import io.beanmapper.spring.model.Product;
import io.beanmapper.spring.model.ProductDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductDtoTest extends AbstractDtoTest {

    @Test
    public void mapToDynamicProductDtoOrgOnlyIdName() throws Exception {
        Product product = createProduct(false);
        Object productDto = dynamicBeanMapper.map(
                product,
                ProductDto.class,
                Arrays.asList("id", "name", "organization.id", "organization.name"));
        String json = new ObjectMapper().writeValueAsString(productDto);
        assertEquals("{\"id\":42,\"name\":\"Aller menscher\",\"organization\":{\"id\":1143,\"name\":\"My Org\"}}", json);
    }

    @Test
    public void mapToDynamicProductDtoWithLists() throws Exception {
        Product product = createProduct(true);
        Object productDto = dynamicBeanMapper.map(
                product,
                ProductDto.class,
                Arrays.asList("id", "name", "assets.id", "assets.name", "artists"));
        String json = new ObjectMapper().writeValueAsString(productDto);
        assertEquals(
                "{\"id\":42,\"name\":\"Aller menscher\"," +
                    "\"assets\":[" +
                        "{\"id\":1138,\"name\":\"Track 1\"}," +
                        "{\"id\":1139,\"name\":\"Track 2\"}," +
                        "{\"id\":1140,\"name\":\"Track 3\"}" +
                    "]," +
                    "\"artists\":[" +
                        "{\"id\":1141,\"name\":\"Artist 1\"}," +
                        "{\"id\":1142,\"name\":\"Artist 2\"}" +
                    "]" +
                "}", json);
    }

    @Test
    public void mapList() throws Exception {
        List<Artist> artists = createArtists();
        Object dto = dynamicBeanMapper.map(artists, ArtistDto.class, Arrays.asList("id", "name"));
        String json = new ObjectMapper().writeValueAsString(dto);
        assertEquals("[{\"id\":1141,\"name\":\"Artist 1\"},{\"id\":1142,\"name\":\"Artist 2\"}]", json);
    }

    @Test
    public void mapListWithNestedEntries() throws Exception {
        List<Product> products = new ArrayList<Product>();
        products.add(createProduct(42L, true));
        products.add(createProduct(43L, true));
        Object dto = dynamicBeanMapper.map(products, ProductDto.class, Arrays.asList("id", "assets.id"));
        String json = new ObjectMapper().writeValueAsString(dto);
        assertEquals("" +
                "[" +
                    "{\"id\":42,\"assets\":[{\"id\":1138},{\"id\":1139},{\"id\":1140}]}," +
                    "{\"id\":43,\"assets\":[{\"id\":1138},{\"id\":1139},{\"id\":1140}]}" +
                "]", json);
    }

    private Product createProduct(boolean includeLists) {
        return createProduct(42L, includeLists);
    }

    private Product createProduct(Long productId, boolean includeLists) {
        Product product = new Product();
        product.setId(productId);
        product.setName("Aller menscher");
        product.setUpc("12345678901");
        product.setInternalMemo("Secret message, not to be let out");

        if (includeLists) {
            product.setAssets(createAssets());
            product.setArtists(createArtists());
        }

        Organization organization = new Organization();
        organization.setId(1143L);
        organization.setName("My Org");
        organization.setContact("Henk");
        product.setOrganization(organization);

        return product;
    }

    private List<Asset> createAssets() {
        List<Asset> assets = new ArrayList<Asset>();
        assets.add(createAsset(1138L, "Track 1", "NL-123-ABCDEFGH"));
        assets.add(createAsset(1139L, "Track 2", "NL-123-ABCDEFGI"));
        assets.add(createAsset(1140L, "Track 3", "NL-123-ABCDEFGJ"));
        return assets;
    }

    private List<Artist> createArtists() {
        List<Artist> artists = new ArrayList<Artist>();
        artists.add(createArtist(1141L, "Artist 1"));
        artists.add(createArtist(1142L, "Artist 2"));
        return artists;
    }

    private Asset createAsset(Long id, String name, String isrc) {
        Asset asset = new Asset();
        asset.setId(id);
        asset.setName(name);
        asset.setIsrc(isrc);
        return asset;
    }

    private Artist createArtist(long id, String name) {
        Artist artist = new Artist();
        artist.setId(id);
        artist.setName(name);
        return artist;
    }

}
