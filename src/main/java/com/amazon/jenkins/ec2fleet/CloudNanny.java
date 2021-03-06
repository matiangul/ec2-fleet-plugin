package com.amazon.jenkins.ec2fleet;

import hudson.Extension;
import hudson.model.PeriodicWork;
import hudson.slaves.Cloud;
import hudson.widgets.Widget;
import jenkins.model.Jenkins;

import java.util.ArrayList;
import java.util.List;

/**
 * User: cyberax
 * Date: 1/11/16
 * Time: 13:21
 */
@Extension
@SuppressWarnings("unused")
public class CloudNanny extends PeriodicWork
{
    @Override public long getRecurrencePeriod() {
        return 10000L;
    }

    @Override protected void doRun() throws Exception {
        // Trigger reprovisioning as well
        Jenkins.getActiveInstance().unlabeledNodeProvisioner.suggestReviewNow();

        final List<FleetStateStats> stats = new ArrayList<FleetStateStats>();
        for(final Cloud cloud : Jenkins.getActiveInstance().clouds) {
            if (!(cloud instanceof EC2FleetCloud))
                continue;

            // Update the cluster states
            final EC2FleetCloud fleetCloud =(EC2FleetCloud) cloud;
            stats.add(fleetCloud.updateStatus());
        }

        for (final Widget w : Jenkins.getInstance().getWidgets()) {
            if (!(w instanceof FleetStatusWidget))
                continue;

            ((FleetStatusWidget)w).setStatusList(stats);
        }
    }
}
