package test.utils;

import com.github.bmsantos.core.cola.story.annotations.ColaInjector;
import com.google.inject.Injector;

public class ColaInjectorTest {

    @ColaInjector
    public Injector i1;

    @ColaInjector
    public Injector i2;
}
