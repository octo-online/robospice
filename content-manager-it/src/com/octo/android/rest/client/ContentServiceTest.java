package com.octo.android.rest.client;

import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

@SmallTest
public class ContentServiceTest extends ServiceTestCase< ContentService > {

    private ContentService contentService;

    public ContentServiceTest() {
        super( ContentService.class );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentService = getService();
    }

}
