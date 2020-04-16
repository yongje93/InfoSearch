package number05;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

			List<String> tempList1 = new ArrayList<>(keyList);
			List<String> tempList2 = new ArrayList<>(keyList2);

			tempList2.removeAll(keyList);
			tempList1.removeAll(keyList2);

			Map<String, Double> top5Vector1 = new HashMap<>();
			Map<String, Double> top5Vector2 = new HashMap<>();

			for (int j = 0; j < keyList.size(); j++) {
				top5Vector1.put(keyList.get(j), totalVector.get(findFile).get(keyList.get(j)));
				top5Vector2.put(keyList2.get(j), totalVector.get(file.getName()).get(keyList2.get(j)));
			}

			for (String str : tempList1)
				top5Vector2.put(str, 0.0);

			for (String str : tempList2)
				top5Vector1.put(str, 0.0);

			List<Double> findList = new ArrayList<>(top5Vector1.values());
			List<Double> compareList = new ArrayList<>(top5Vector2.values());

			Double[] find = findList.toArray(new Double[findList.size()]);
			Double[] compare = compareList.toArray(new Double[compareList.size()]);

			cos_result.put(file.getName(), cosineSimilarity(find, compare));
		}
		return cos_result;
	}

	// 코사인 유사도 계산
	public static double cosineSimilarity(Double[] find, Double[] compare) {
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
				int com = o2.getValue().compareTo(o1.getValue());
				if (o2.getValue() == o1.getValue()) {
					com = o2.getKey().compareTo(o1.getKey());
				}
				return com;
			}
		});

		List<String> top5 = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			top5.add(list.get(i).getKey());
		}
		return top5;
	}
}
