package org.opengis.cite.ogcapigdc10;

import java.util.List;

import org.testng.IMethodSelector;
import org.testng.IMethodSelectorContext;
import org.testng.ITestNGMethod;

public class OAPIPMethodSelector implements IMethodSelector {

    /**
     * 
     */
    private static final long serialVersionUID = 6193127149298117170L;

    @Override
    public boolean includeMethod(IMethodSelectorContext context,
            ITestNGMethod method,
            boolean isTestMethod) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setTestMethods(List<ITestNGMethod> testMethods) {
        // TODO Auto-generated method stub

    }

}
