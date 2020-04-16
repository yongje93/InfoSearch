package number05;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Co_occurence {
	public Map<String, Integer> calculateWordCo(List<String[]> docArrays, String findTerm, int window_size) {
		Map<String, Integer> result = new HashMap<>();
		for (String[] array : docArrays) {
			for (int i = 0; i < array.length; i++) {
				// 일단 해당 문자의 위치 i 찾음
				if (array[i].equals(findTerm)) {
					// 윈도우 크기보다 작을 때
					if (i < window_size) {
						for (int j = 0; j < i + window_size; j++) {
							if (array[j].equals(findTerm))
								continue;
							else if (!result.containsKey(array[j])) {
								result.put(array[j], 1);
							} else {
								result.put(array[j], result.get(array[j]) + 1);
							}
						}
					} // 윈도우 크기보다 클때
					else if (window_size > array.length - i - 1) {
						for (int j = i - window_size; j < array.length; j++) {
							if (array[j].equals(findTerm))
								continue;
							else if (!result.containsKey(array[j])) {
								result.put(array[j], 1);
							} else {
								result.put(array[j], result.get(array[j]) + 1);
							}
						}
					} // 둘다 아닐때
					else {
						for (int j = i - window_size; j <= i + window_size; j++) {
							if (array[j].equals(findTerm))
								continue;
							else if (!result.containsKey(array[j])) {
								result.put(array[j], 1);
							} else {
								result.put(array[j], result.get(array[j]) + 1);
							}
						}
					}
				}
			}
		}
		return result;
	}
}
