package com.example.core_di.tcp;

import static org.junit.Assert.assertEquals;

import com.example.application.session.OmokStoneType;

import org.junit.Test;

public class PlacementedStoneTypeMapperTest {

    @Test
    public void fromNetworkLabel_mapsKnownValues() {
        assertEquals(OmokStoneType.RED, StoneTypeMapper.fromNetworkLabel("Player1"));
        assertEquals(OmokStoneType.BLOCKER, StoneTypeMapper.fromNetworkLabel("5"));
    }

    @Test
    public void fromNetworkLabel_unknownValueDefaultsToUnknown() {
        assertEquals(OmokStoneType.UNKNOWN, StoneTypeMapper.fromNetworkLabel("mystery"));
    }

    @Test
    public void fromCellValue_mapsExpectedEntries() {
        assertEquals(OmokStoneType.EMPTY, StoneTypeMapper.fromCellValue(-1));
        assertEquals(OmokStoneType.EMPTY, StoneTypeMapper.fromCellValue(0xFF));
        assertEquals(OmokStoneType.YELLOW, StoneTypeMapper.fromCellValue(2));
        assertEquals(OmokStoneType.UNKNOWN, StoneTypeMapper.fromCellValue(6));
    }
}
