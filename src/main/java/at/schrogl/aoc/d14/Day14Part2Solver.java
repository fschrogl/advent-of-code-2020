package at.schrogl.aoc.d14;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day14Part2Solver {

    private Map<Long, Long> memory = new HashMap<>();
    private char[] mask;

    public void computeInput(List<String> lines) {
        for (String line : lines) {
            if (line.startsWith("mask"))
                mask = line.split("=")[1].trim().toCharArray();
            else
                computeMemory(line);
        }
    }

    private void computeMemory(String line) {
        String[] valueSplit = line.split("=");
        Long originalAddress = Long.valueOf(valueSplit[0].trim().substring(4, valueSplit[0].trim().length() - 1));
        Long value = Long.valueOf(valueSplit[1].trim());

        String addressBinary = toBinary(originalAddress.toString());
        String addressResult = applyBitmask(addressBinary);
        List<Integer> floatingBitIndexes = getFloatingBitIndexes(addressResult);

        double numberOfPermutations = Math.pow(2, floatingBitIndexes.size());
        for (int i = 0; i < numberOfPermutations; i++) {
            String currentPermutationBits = Integer.toBinaryString(i);
            currentPermutationBits = padleft(currentPermutationBits, floatingBitIndexes.size());
            String currentBinaryAddressPermutation = replaceXesWithPermutationBits(addressResult, currentPermutationBits, floatingBitIndexes);
            memory.put(fromBinary(currentBinaryAddressPermutation), value);
        }
    }

    private String replaceXesWithPermutationBits(String addressResult, String currentPermutationBits, List<Integer> floatingBitIndexes) {
        StringBuilder sb = new StringBuilder(addressResult);
        for (int i = 0; i < floatingBitIndexes.size(); i++) {
            sb.setCharAt(floatingBitIndexes.get(i), currentPermutationBits.charAt(i));
        }
        return sb.toString();
    }


    private String padleft(String currentBinaryAddressPermutation, int size) {
        if (currentBinaryAddressPermutation.length() < size) {
            StringBuilder sb = new StringBuilder(size);
            while (sb.length() < (size - currentBinaryAddressPermutation.length())) sb.append('0');
            return sb.append(currentBinaryAddressPermutation).toString();
        }
        return currentBinaryAddressPermutation;
    }

    private List<Integer> getFloatingBitIndexes(String addressResult) {
        List<Integer> indexes = new ArrayList<>();
        char[] addressResultArray = addressResult.toCharArray();
        for (int i = 0; i < addressResultArray.length; i++) {
            if (addressResultArray[i] == 'X') {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private String applyBitmask(String binaryValue) {
        char[] input = binaryValue.toCharArray();
        char[] result = new char[36];

        for (int i = 0, j = (result.length - input.length); i < result.length; i++) {
            result[i] = (i < j) ? '0' : input[i - j];
        }
        for (int i = 0; i < mask.length; i++) {
            if (mask[i] != '0') {
                result[i] = mask[i];
            }
        }
        return String.valueOf(result);
    }

    public long getResultSum() {
        return memory.values().stream().mapToLong(Long::valueOf).sum();
    }

    public String getMemoryContents() {
        return memory.entrySet().stream()
            .map(e -> String.format("%d=%d", e.getKey(), e.getValue()))
            .collect(Collectors.joining(", "));
    }

    private String toBinary(String value) {
        return Long.toBinaryString(Long.parseLong(value.trim()));
    }

    private Long fromBinary(String binaryString) {
        return Long.parseLong(binaryString, 2);
    }
}
