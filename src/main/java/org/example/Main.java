package org.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static java.lang.Math.max;


public class Main {

    static Map<Long, String> inds = new HashMap<>(); // работаем везде с индексами строк, а не со строками для удобства

    static Map<Entry, Integer> connects = new HashMap<>(); // Для каждого Entry храним сколько раз он встречается
    // Entry - это пара позиции и значения, она определяет коннект между строками

    static Map<Entry, List<Long>> interesting = new HashMap<>(); // тут уже для каждого Entry храним строки,
    //которых он встречается. Замечу что тут храним с условием что в листе больше одного элемента
    //(игнорим группы из 1  элемента)

    static Map<Long, List<Long>> graph = new HashMap<>(); // из этих коннектов можно построить граф,
    // сведя задачу к поиску размеров компонент связности

    static Set<Long> used = new HashSet<>(); // нужно чтобы понять во время дфса были ли мы в вершине или нет

    static List<List<Entry>> lines = new ArrayList<>(); // когда распарсим строки кладем их сюда

    static List<Entry> parseLine(String line) {  // получает сырую строку из input, делает проверку и возвращает в
        //удобном формате

        if (!line.matches("(\"[0-9]*\";)+\"[0-9]*\"")) {
            return List.of();
        }
        List<String> lines = List.of(line.split(";"));
        List<Entry> vals = new ArrayList<>();
        for (int pos = 0; pos < lines.size(); pos++) {
            if (lines.get(pos).length() != 2) {
                Long elem = Long.parseLong(lines.get(pos).substring(1, lines.get(pos).length() - 1));
                vals.add(new Entry(pos, elem));
            }
        }

        return vals;
    }


    static Set<Long> dfs(long v, Set<Long> group) {
        used.add(v);
        group.add(v);
        for (Long u : graph.get(v)) { // идем в детей, сливаем их ответ воедино, добавляем текущую вершину и return
            if (used.contains(u)) continue;
            group.addAll(dfs(u, new HashSet<>()));
        }
        return group;
    }

    public static void main(String[] args) {
        String filePath = args[0];
        long start = System.currentTimeMillis();
        int cnt = 0;
        int sz = 0;
        long ind = 0;
        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                cnt++;
                sz += line.length();
                ind++;

                inds.put(ind, line);

                List<Entry> curEntires = parseLine(line);
                for (Entry cur : curEntires) {
                    if (!connects.containsKey(cur)) {
                        connects.put(cur, 1);
                    } else connects.put(cur, (connects.get(cur) + 1));
                }

                lines.add(curEntires);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Entry key : connects.keySet()) {
            long t = connects.get(key);
            if (t > 1) interesting.put(key, new ArrayList<>()); // игнорим группы из 1 строки
        }

        for (long index = 1; index <= lines.size(); index++) {
            List<Entry> curEntries = lines.get((int) (index - 1));
            for (Entry entry : curEntries) {
                if (interesting.containsKey(entry)) {
                    interesting.get(entry).add(index);
                }
            }
        }

        for (Entry key : interesting.keySet()) {
            List<Long> vals = interesting.get(key);
            for (Long v : vals) {
                for (Long u : vals) {
                    if (Objects.equals(v, u)) continue;
                    if (!graph.containsKey(u)) graph.put(u, new ArrayList<>());
                    if (!graph.containsKey(v)) graph.put(v, new ArrayList<>());
                    graph.get(v).add(u);
                }
            }
        }

        List<Set<Long>> ans = new ArrayList<>();

        for (Long v : graph.keySet()) {
            if (used.contains(v)) continue;
            ans.add(dfs(v, new HashSet<>()));
        }

        System.out.println("Meg used=" + (Runtime.getRuntime().totalMemory() -
                Runtime.getRuntime().freeMemory()) / (1000 * 1000) + "M");

        System.out.println(ans.size());
        ans.sort(Comparator.comparing(x -> -x.size()));
        int counter = 0;
        for (Set<Long> group : ans) {
            System.out.println("Group " + (++counter));
            for (Long index : group) {
                System.out.println("\t " + inds.get(index));
            }
        }


        System.out.println("lines: " + cnt);
        System.out.println("chars: " + sz);


        long end = (System.currentTimeMillis() - start);
        System.out.println("time spent: " + end);
    }
}