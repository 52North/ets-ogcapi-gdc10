package org.opengis.cite.ogcapigdc10;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        Boolean testOapip =
                (Boolean) result.getTestContext().getSuite().getAttribute(SuiteAttribute.TESTOAPIP.getName());
        if (testOapip != null && testOapip == false) {
            if (result.getInstanceName().equals("org.opengis.cite.ogcapiprocesses10.process.Process")
                    || result.getInstanceName().equals("org.opengis.cite.ogcapiprocesses10.jobs.Jobs")) {
                throw new SkipException("OGC API - Processes not supported by this API.");
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestFailure(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
        // TODO Auto-generated method stub

    }

}
