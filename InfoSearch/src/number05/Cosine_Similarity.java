package number05;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Cosine_Similarity {
	public Map<String, Double> getCosMap(File[] fileList, String findFile, Map<String, HashMap<String, Double>> totalVector) {
		Map<String, Double> cos_result = new HashMap<>();
		for (File file : fileList) {
			if (findFile.equals(file.getName()))
				continue;
			Map<String, Double> findVector = totalVector.get(findFile);
			Map<String, Double> compareVector = totalVector.get(file.getName());

			// 상위 5개의 키 가져오기
			List<String> keyList = new ArrayList<>(sortByValue(findVector));
			List<String> keyList2 = new ArrayList<>(sortByValue(compareVector));

			List<String> allKeyList = new ArrayList<>();
			allKeyList.addAll(keyList);
			allKeyList.addAll(keyList2);

			Set<String> allkey = new HashSet<>(allKeyList);

			Map<String, Double> top5Vector1 = new HashMap<>();
			Map<String, Double> top5Vector2 = new HashMap<>();

			for (int j = 0; j < keyList.size(); j++) {
				top5Vector1.put(keyList.get(j), totalVector.get(findFile).get(keyList.get(j)));
			}
			for (int j = 0; j < keyList2.size(); j++) {
				top5Vector2.put(keyList2.get(j), totalVector.get(findFile).get(keyList.get(j)));
			}

			double[] find = new double[allkey.size()];
			double[] compare = new double[allkey.size()];
			
			int j = 0;
			for (String key : allkey) {
				if (!top5Vector1.containsKey(key)) {
					top5Vector1.put(key, 0.0);
				}
				if (!top5Vector2.containsKey(key)) {
					top5Vector2.put(key, 0.0);
				}
				find[j] = top5Vector1.get(key);
				compare[j] = top5Vector2.get(key);
				j++;
			}

			cos_result.put(file.getName(), cosineSimilarity(find, compare));
		}
		return cos_result;
	}

	// 코사인 유사도 계산
	public static double cosineSimilarity(double[] find, double[] compare) {
		if (find == null || compare == null || find.length == 0 || compare.length == 0
				|| find.length != compare.length) {
			return 2;
		}

		double sumProduct = 0;
		double sumASq = 0;
		double sumBSq = 0;
		for (int i = 0; i < find.length; i++) {
			sumProduct += find[i] * compare[i];
			sumASq += find[i] * find[i];
			sumBSq += compare[i] * compare[i];
		}
		if (sumASq == 0 && sumBSq == 0) {
			return 2.0;
		}
		return sumProduct / (Math.sqrt(sumASq) * Math.sqrt(sumBSq));
	}

	// 상위 5개
	public static List<String> sortByValue(Map<String, Double> vector) {
		List<Entry<String, Double>> list = new ArrayList<>(vector.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				int comparision = ((o2.getValue() > o1.getValue() ? 1 : -1));
				return comparision == 0 ? o2.getKey().compareTo(o1.getKey()) : comparision;
			}
		});

		List<String> top5 = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			top5.add(list.get(i).getKey());
		}
		return top5;
	}
}
