package kr.hhplus.be.server.global.lock;

import java.util.List;
import java.util.stream.Collectors;

public class LockKeyGenerator {
    public static String generate(String feature, List<Long> ids) {
        String idsWithDelimiter = ids.stream().map(Object::toString).collect(Collectors.joining(":"));
        return String.join(":", feature, idsWithDelimiter);
    }
}
