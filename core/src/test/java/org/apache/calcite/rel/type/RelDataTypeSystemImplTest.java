package org.apache.calcite.rel.type;

import org.apache.calcite.sql.type.SqlTypeName;
import org.junit.Test;

import static org.junit.Assert.*;

public class RelDataTypeSystemImplTest {

    RelDataTypeSystem relDataTypeSystem = RelDataTypeSystem.DEFAULT;

    /**
     * 返回给定类型的最大比例
     */
    @Test
    public void getMaxScale() {
        int maxScale = relDataTypeSystem.getMaxScale(SqlTypeName.DECIMAL);
        System.out.println(maxScale);

    }

    @Test
    public void getDefaultPrecision() {
    }

    @Test
    public void getMaxPrecision() {
    }

    @Test
    public void getMaxNumericScale() {
    }

    @Test
    public void getMaxNumericPrecision() {
    }

    @Test
    public void getLiteral() {
    }

    @Test
    public void isCaseSensitive() {
    }

    @Test
    public void isAutoincrement() {
    }

    @Test
    public void getNumTypeRadix() {
    }

    @Test
    public void deriveSumType() {
    }

    @Test
    public void deriveAvgAggType() {
    }

    @Test
    public void deriveCovarType() {
    }

    @Test
    public void deriveFractionalRankType() {
    }

    @Test
    public void deriveRankType() {
    }

    @Test
    public void isSchemaCaseSensitive() {
    }

    @Test
    public void shouldConvertRaggedUnionTypesToVarying() {
    }

    @Test
    public void allowExtendedTrim() {
    }
}