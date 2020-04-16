package number04;

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
import java.util.Map.Entry;
import java.util.Scanner;

public class TF_IDF_COS {
	private static List<String> allTerms = new ArrayList<>();
	private static List<String[]> docArrays = new ArrayList<>();

	public static void main(String[] args) {
		TF_IDF_COS tf_idf_cos = new TF_IDF_COS();

		String path = System.getProperty("user.dir");
		Scanner scan = new Scanner(System.in);

		System.out.print("문서 이름을 입력하세요 : ");
		String findFile = scan.next();

		File[] fileList = tf_idf_cos.parseFiles(path + "/test");

		Map<String, HashMap<String, Double>> totalVector = new HashMap<>();
		Map<String, Double> cos_result = new HashMap<>();

		int i = 0;
		for (String[] string : docArrays) {
			HashMap<String, Double> docVector = new HashMap<>();
			for (String s : string) {
				// 알파벳에 가중치 tf-idf로 주기
				List<Double> result = tf_idf_cos.calculateTFIDF(s);
				if (!docVector.containsKey(s)) {
					docVector.put(s, result.get(i));
				} else {
					continue;
				}
			}
			totalVector.put(fileList[i++].getName(), docVector);
		}

		for (File file : fileList) {
			if (findFile.equals(file.getName()))
				continue;
			Map<String, Double> findVector = totalVector.get(findFile);
			Map<String, Double> compareVector = totalVector.get(file.getName());

			// 상위 5개의 키 가져오기
			List<String> keyList = new ArrayList<>(tf_idf_cos.sortByValue(findVector));
			List<String> keyList2 = new ArrayList<>(tf_idf_cos.sortByValue(compareVector));

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

			cos_result.put(file.getName(), tf_idf_cos.cosineSimilarity(find, compare));

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
			System.out.printf("%s:%.4f, ", key, value);
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

	// TF 계산
	public double calculateTF(String[] terms, String findTerm) {
		double count = 0;
		for (String s : terms) {
			if (s.equals(findTerm)) {
				count++;
			}
		}
		return count / terms.length;
	}

	// IDF 계산
	public double calculateIDF(List<String[]> allTerms, String findTerm) {
		double count = 0;
		for (String[] strings : allTerms) {
			for (String str : strings) {
				if (str.equals(findTerm)) {
					count++;
					break;
				}
			}
		}
		return Math.log(1 + allTerms.size() / count);
	}

	// TF-IDF 계산
	public List<Double> calculateTFIDF(String findTerm) {
		List<Double> result = new ArrayList<>();
		double tf;
		double idf;
		for (String[] docTerm : docArrays) {
			tf = calculateTF(docTerm, findTerm);
			idf = calculateIDF(docArrays, findTerm);
			result.add(tf * idf);
		}
		return result;
	}

	// 코사인 유사도 계산
	public double cosineSimilarity(Double[] find, Double[] compare) {
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
	public List<String> sortByValue(Map<String, Double> vector) {
		List<Entry<String, Double>> list = new ArrayList<>(vector.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				int comparision = ((o2.getValue() > o1.getValue() ? 1 : -1));
//                return comparision == 0 ? o2.getKey().compareTo(o1.getKey()) : comparision;
				return comparision == 0 ? o2.getKey().compareTo(o1.getKey()) * -1 : comparision;
			}
		});

		List<String> top5 = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			top5.add(list.get(i).getKey());
		}
		return top5;
	}
}
