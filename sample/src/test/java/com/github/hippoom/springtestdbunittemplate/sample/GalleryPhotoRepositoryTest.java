package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.Test;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT_UNORDERED;


public class GalleryPhotoRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private GalleryPhotoRepository subject;

    @DatabaseSetup("classpath:gallery_photo_save_before.xml")
    @ExpectedDatabase(
            value = "classpath:gallery_photo_save_after.xml",
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenSave_() throws Exception {
        final GalleryPhoto toBeSaved = newTransactionTemplate().execute(new TransactionCallback<GalleryPhoto>() {
            @Override
            public GalleryPhoto doInTransaction(TransactionStatus status) {

                final GalleryPhoto prototype = subject.findOne("1");
                final GalleryPhoto photo = cloneFrom("2", "b.jpg", prototype);
                return photo;
            }
        });
        subject.save(toBeSaved);
    }

    private GalleryPhoto cloneFrom(final String toBeSavedId, final String fileName,  final GalleryPhoto prototype) {

        final PropertyMap<GalleryPhoto, GalleryPhoto> propertyMap = new PropertyMap<GalleryPhoto, GalleryPhoto>() {
            @Override
            protected void configure() {
                map(toBeSavedId, destination.getId());
                map(fileName, destination.getFileName());
            }
        };
        getModelMapper().addMappings(propertyMap);
        return getModelMapper().map(prototype, GalleryPhoto.class);
    }


}
