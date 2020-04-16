package number03;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Cosine_Similarity {
	private static List<String> allTerms = new ArrayList<>();
	private static List<String[]> docArrays = new ArrayList<>();

	public static void main(String[] args) {
		Cosine_Similarity cos = new Cosine_Similarity();

		String path = System.getProperty("user.dir");
		Scanner scan = new Scanner(System.in);

		System.out.print("문서 이름을 입력하세요 : ");
		String findFile = scan.next();

		File[] fileList = cos.parseFiles(path + "/test");

		Map<String, HashMap<String, Integer>> totalVector = new HashMap<>();
		Map<String, Double> cos_result = new HashMap<>();

		int i = 0;
		for (String[] string : docArrays) {
			HashMap<String, Integer> docVector = new HashMap<>();
			for (String s : string) {
				// 알파벳에 가중치 1 주기
				if (!docVector.containsKey(s)) {
					docVector.put(s, 1);
				} else {
					continue;
				}
			}
			totalVector.put(fileList[i++].getName(), docVector);
		}

		for (File file : fileList) {
			if (findFile.equals(file.getName()))
				continue;
			Map<String, HashMap<String, Integer>> map = new HashMap<>(totalVector);
			List<String> keyList = new ArrayList<>(map.get(findFile).keySet());
			List<String> keyList2 = new ArrayList<>(map.get(file.getName()).keySet());

			List<String> tempList1 = new ArrayList<>(keyList);
			List<String> tempList2 = new ArrayList<>(keyList2);

			tempList1.removeAll(keyList2);
			tempList2.removeAll(keyList);

			// 각각 없는 알파벳에 가중치 0으로 주기
			for (String str : tempList1)
				map.get(file.getName()).put(str, 0);

			for (String str : tempList2)
				map.get(findFile).put(str, 0);

			List<Integer> findList = new ArrayList<>(map.get(findFile).values());
			List<Integer> compareList = new ArrayList<>(map.get(file.getName()).values());

			Integer[] find = findList.toArray(new Integer[findList.size()]);
			Integer[] compare = compareList.toArray(new Integer[compareList.size()]);

			cos_result.put(file.getName(), cos.cosineSimilarity(find, compare));
		}

		List<String> keySetList = new ArrayList<>(cos_result.keySet());
		Collections.sort(keySetList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return cos_result.get(o2).compareTo(cos_result.get(o1));
			}
		});

		System.out.print(findFile + " -> ");
		for (String key : keySetList) {
			double value = cos_result.get(key);
			System.out.printf("%s:%.3f, ", key, value);
		}
		scan.close();
	}

	public File[] parseFiles(String filePath) {
		File[] files = new File(filePath).listFiles();
		List<String> fileList = new ArrayList<>();
		BufferedReader br = null;
		try {
			for (File file : files) {
				if (file.getName().endsWith(".data")) {
					fileList.add(file.getName());
					br = new BufferedReader(new FileReader(file));
					StringBuilder sb = new StringBuilder();
					String s = null;
					while ((s = br.readLine()) != null) {
						sb.append(s);
					}
					String[] tokenizedTerms = sb.toString().split(" ");
					for (String term : tokenizedTerms) {
						if (!allTerms.contains(term)) {
							allTerms.add(term);
						}
					}
					docArrays.add(tokenizedTerms);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}

	// 코사인 유사도 계산
	public double cosineSimilarity(Integer[] find, Integer[] compare) {
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
}
