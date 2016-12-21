package com.shollmann.android.igcparser;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;


public class ParserUnitTest {
    @Mock
    Context context;
    @Mock
    AssetManager assetManager;
    @Mock
    InputStream inputStream;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(assetManager).when(context).getAssets();
        URL resource = ParserUnitTest.class.getClassLoader().getResource("sample1.igc");
        // to be used  MyClass
        // inside the method I want to be tested there is this statement :
        InputStream inputStream = new FileInputStream(resource.getPath());
        Mockito.doReturn(inputStream).when(assetManager).open(Matchers.anyString());

    }


    @Test
    public void parse_isCorrect() throws Exception {
//        Parser.parse(getContext(),)
    }

}