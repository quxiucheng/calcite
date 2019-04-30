package calcite.flink.v1_7.table.calcite;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.rel.metadata.JaninoRelMetadataProvider;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rex.RexBuilder;

/**
 * @author quxiucheng
 * @date 2019-01-22 16:32:00
 */
public class FlinkRelOptClusterFactory {

    static RelOptCluster create(RelOptPlanner planner, RexBuilder rexBuilder) {
        RelOptCluster cluster =  RelOptCluster.create(planner, rexBuilder);
        // cluster.setMetadataProvider(FlinkDefaultRelMetadataProvider.INSTANCE);
        // just set metadataProvider is not enough, see
        // https://www.mail-archive.com/dev@calcite.apache.org/msg00930.html
        RelMetadataQuery.THREAD_PROVIDERS.set(
                JaninoRelMetadataProvider.of(cluster.getMetadataProvider()));
        return cluster;
    }
}
