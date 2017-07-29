package com.github.marschall.stiletto.tests.hierarchy;

import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@AdviseBy(BeforeCountingAspect.class)
public class LeafClassSameInterface extends IntermediateClassSameInterface implements IntermediateInterface {

}
