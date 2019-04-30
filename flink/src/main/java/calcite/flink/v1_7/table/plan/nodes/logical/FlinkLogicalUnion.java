package calcite.flink.v1_7.table.plan.nodes.logical;

import calcite.flink.v1_7.table.plan.FlinkConventions;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCost;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;
import org.apache.calcite.rel.core.SetOp;
import org.apache.calcite.rel.core.Union;
import org.apache.calcite.rel.logical.LogicalUnion;
import org.apache.calcite.rel.metadata.RelMetadataQuery;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author quxiucheng
 * @date 2019-02-21 17:17:00
 */
public class FlinkLogicalUnion extends Union {

    public static ConverterRule CONVERTER = new FlinkLogicalUnionConverter();

    public static FlinkLogicalUnion create(List<RelNode> inputs, Boolean all) {
        RelOptCluster cluster = inputs.get(0).getCluster();
        RelTraitSet traitSet = cluster.traitSetOf(FlinkConventions.LOGICAL);
        return new FlinkLogicalUnion(cluster, traitSet, inputs, all);
    }

    private RelOptCluster cluster;
    private RelTraitSet traits;
    private List<RelNode> inputs;
    private boolean all;

    protected FlinkLogicalUnion(RelOptCluster cluster, RelTraitSet traits, List<RelNode> inputs, boolean all) {
        super(cluster, traits, inputs, all);
        this.cluster = cluster;
        this.inputs = inputs;
    }

    @Override
    public SetOp copy(RelTraitSet traitSet, List<RelNode> inputs, boolean all) {
        return new FlinkLogicalUnion(cluster, traitSet, inputs, all);
    }

    @Override
    public RelOptCost computeSelfCost(RelOptPlanner planner, RelMetadataQuery metadata) {
        List<RelNode> children = this.inputs;
        Double rowCnt = 0.0;
        for (RelNode child : children) {
            rowCnt += metadata.getRowCount(child);

        }
        return planner.getCostFactory().makeCost(rowCnt, 0, 0);
    }

    private static class FlinkLogicalUnionConverter extends ConverterRule {


        public FlinkLogicalUnionConverter() {
            super(LogicalUnion.class, Convention.NONE, FlinkConventions.LOGICAL, "FlinkLogicalUnionConverter");
        }

        @Override
        public boolean matches(RelOptRuleCall call) {
            LogicalUnion union = call.rel(0);
            return union.all;
        }

        @Override
        public RelNode convert(RelNode rel) {
            LogicalUnion union = (LogicalUnion) rel;
            RelTraitSet traitSet = rel.getTraitSet().replace(FlinkConventions.LOGICAL);
            List<RelNode> newInputs = union.getInputs().stream()
                    .map(input -> RelOptRule.convert(input, FlinkConventions.LOGICAL)).collect(Collectors.toList());
            return new FlinkLogicalUnion(rel.getCluster(), traitSet, newInputs, union.all);
        }
    }
}


