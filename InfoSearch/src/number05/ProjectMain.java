package number05;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProjectMain {
	private static List<String> allTerms = new ArrayList<>();
	private static List<String[]> docArrays = new ArrayList<>();

	public static void main(String[] args) {
		ProjectMain project = new ProjectMain();
		String path = System.getProperty("user.dir");
		Scanner scan = new Scanner(System.in);

		File[] fileList = project.parseFiles(path + "/test");

		System.out.println("1 텍스트(알파벳) -> TF-IDF");
		System.out.println("2 텍스트(알파벳) -> Co-occurence");
		System.out.println("3 파일명 -> Cosine Similarity");
		System.out.println("4 디렉토리 경로 -> 새로운 텍스트 파일 추가");
		System.out.println("5 -> 종료");
		while (true) {
			System.out.print("\n입력하세요 : ");
			String[] input = scan.nextLine().split(" ");
			// TF-IDF
			if ("1".equals(input[0])) {
				TF_IDF tf_idf = new TF_IDF();
				String findTerm = input[1];
				List<Double> result = new ArrayList<>();

				result = tf_idf.calculateTFIDF(docArrays, findTerm);

				System.out.println(findTerm + "의 TF-IDF 스코어");
				int i = 0;
				for (Double data : result) {
					System.out.printf("%s\t %.4f\n", fileList[i++].getName(), data);
				}

			} // Co_occurence
			else if ("2".equals(input[0])) {
				Co_occurence co = new Co_occurence();
				int windowSize = 2; // 윈도우 크기
				String findTerm = input[1];

				Map<String, Integer> result = co.calculateWordCo(docArrays, findTerm, windowSize);

				List<String> keySetList = new ArrayList<>(result.keySet());
				Collections.sort(keySetList, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return result.get(o2).compareTo(result.get(o1));
					}
				});

				System.out.print(findTerm + " -> ");
				for (String key : keySetList) {
					int value = result.get(key);
					System.out.print(key + ":" + value + ", ");
				}
				System.out.println("");

			} // Cosine_Similarity
			else if ("3".equals(input[0])) {
				TF_IDF tf_idf = new TF_IDF();
				Cosine_Similarity cos = new Cosine_Similarity();

				String findFile = input[1];
				Map<String, HashMap<String, Double>> totalVector = new HashMap<>();

				int i = 0;
				for (String[] string : docArrays) {
					HashMap<String, Double> docVector = new HashMap<>();
					for (String s : string) {
						// 알파벳에 가중치 tf-idf로 주기
						List<Double> result = tf_idf.calculateTFIDF(docArrays, s);
						if (!docVector.containsKey(s)) {
							docVector.put(s, result.get(i));
						} else {
							continue;
						}
					}
					totalVector.put(fileList[i++].getName(), docVector);
				}

				Map<String, Double> cos_result = cos.getCosMap(fileList, findFile, totalVector);

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

				System.out.println("");
			} // 디렉토리 추가
			else if ("4".equals(input[0])) {
				File sourceF = new File(input[1]); // 복사할 디렉토리 위치
				//File sourceF = new File(path + "/" + input[1]); // 상대경로로 할 때
				File targetF = new File(path + "/test"); // 복사될 디렉토리 위치
				// 파일 복사
				project.copy(sourceF, targetF);

				fileList = null;
				allTerms.clear();
				docArrays.clear();

				fileList = project.parseFiles(path + "/test");
				System.out.println("파일 추가 완료");
			} // 종료
			else if ("5".equals(input[0])) {
				System.out.println("종료합니다.");
				System.exit(0);
				scan.close();
			} else {
				System.out.println("1~5중 입력해주세요.");
			}
		}
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

	public void copy(File sourceF, File targetF) {
		File[] target_file = sourceF.listFiles();
		for (File file : target_file) {
			File temp = new File(targetF.getAbsolutePath() + File.separator + file.getName());
			if (file.isDirectory()) {
				temp.mkdir();
				copy(file, temp);
			} else {
				FileInputStream fis = null;
				FileOutputStream fos = null;
				try {
					fis = new FileInputStream(file);
					fos = new FileOutputStream(temp);
					byte[] b = new byte[4096];
					int cnt = 0;
					while ((cnt = fis.read(b)) != -1) {
						fos.write(b, 0, cnt);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fis.close();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
