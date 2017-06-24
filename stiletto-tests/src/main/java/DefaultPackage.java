import com.github.marschall.stiletto.api.generation.AdviseBy;
import com.github.marschall.stiletto.tests.BeforeCountingAspect;

@AdviseBy(BeforeCountingAspect.class)
public class DefaultPackage {

  public String simpleMethod() {
    return "not recommended";
  }

}
