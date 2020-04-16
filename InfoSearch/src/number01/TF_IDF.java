package number01;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TF_IDF {
	private List<String> allTerms = new ArrayList<>();
	private List<String[]> docArrays = new ArrayList<>();

	public static void main(String[] args) {
		TF_IDF tf_idf = new TF_IDF();
		
		String path = System.getProperty("user.dir");
		Scanner scan = new Scanner(System.in);

		System.out.print("알파벳을 입력하세요 : ");
		String findTerm = scan.next();

		File[] fileList = tf_idf.parseFiles(path + "/test");
		List<Double> result = tf_idf.calculateTFIDF(findTerm);

		System.out.println(findTerm + "의 TF-IDF 스코어");
		int i = 0;
		for (Double data : result) {
			System.out.printf("%s\t %.4f\n", fileList[i++].getName(), data);
		}
		scan.close();
	}

	private File[] parseFiles(String filePath) {
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
}
