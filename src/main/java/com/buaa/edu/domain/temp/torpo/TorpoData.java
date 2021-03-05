package com.buaa.edu.domain.temp.torpo;

import com.buaa.edu.domain.temp.Pair;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TorpoData {
    public HashMap<String, TorpoRoute> route_map;

    public HashMap<String, ArrayList<TorpoRoute>> torpoDeviceMap;

    private ArrayList<Pair<Integer, Integer>> netEleLinks;

    private ArrayList<Integer> netEleList;

    public TorpoData() {
        route_map = new HashMap<String, TorpoRoute>();
        torpoDeviceMap = new HashMap<>();
        netEleList = new ArrayList<>();
        netEleLinks = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TorpoData torpoData = (TorpoData) o;
        return Objects.equals(route_map, torpoData.route_map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route_map);
    }

    public void ReadTorpoInfo(Workbook book) {
        try {
            // 创建输入流，读取Excel
            Workbook wb = book;

            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            /*
            for (int index = 0; index < sheet_size - 1; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                TorpoRoute route = new TorpoRoute();
                route.ReadTorpoRoute(sheet);

                route_map.put(sheet.getName(), route);

                WritableSheet sheet_out = wb_out.createSheet(sheet.getName(), 0);
                route.PrintOutRouteMsg(sheet_out);

            }
            */
            for (int i = 0; i < sheet_size - 1; i++) {
                Sheet sheet = wb.getSheet(i);
                String[] route_name = TorpoRoute.GetRouteNameBySheet(sheet);
                TorpoRoute route;
                if (route_map.containsKey(route_name[0])) {
                    route = route_map.get(route_name[0]);
                    route.ReadTorpoRoute(sheet, true);
                } else if (route_map.containsKey(route_name[1])) {
                    route = route_map.get(route_name[1]);
                    route.ReadTorpoRoute(sheet, false);
                } else {
                    route = new TorpoRoute();
                    route.ReadTorpoRoute(sheet, true);
                    route_map.put(route_name[0], route);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add new Data structure for over-route ports
        // Also for torpo Connection between netEle

        for (String str: route_map.keySet()) {
            TorpoRoute torpoRoute = route_map.get(str);
            HashMap<String, TorpoDevice> deviceMap = torpoRoute.getDevice_map();
            for (String deviceName: deviceMap.keySet()) {
                if (this.torpoDeviceMap.containsKey(deviceName)) {
                    ArrayList<TorpoRoute> routes = this.torpoDeviceMap.get(deviceName);
                    routes.add(torpoRoute);
                } else {
                    ArrayList<TorpoRoute> routes = new ArrayList<>();
                    routes.add(torpoRoute);
                    this.torpoDeviceMap.put(deviceName, routes);
                }
            }
            this.netEleLinks.addAll(torpoRoute.getNetEleLinks());
            this.netEleList.addAll(torpoRoute.getNetEles());

        }

        try {

            File out_file = new File("Torpo_TYPE_NEW_LINUX-03.xls");
            WritableWorkbook wb_out = Workbook.createWorkbook(out_file);
            for (String str:
                route_map.keySet()) {
                TorpoRoute route = route_map.get(str);
                WritableSheet sheet = wb_out.createSheet(route.GetRouteLabel(), 0);
                route.PrintOutRouteMsg(sheet);
            }
            wb_out.write();
            wb_out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ReadTorpoInfo(String filename) {
        File in_file = new File(filename);
        //File out_file = new File("Torpo_route.xls");

        File out_file = new File("Torpo_TYPE_NEW_LINUX-03.xls");

        try {
            this.ReadTorpoInfo(Workbook.getWorkbook(in_file));
        } catch (Exception e) {
            System.out.println("Error happens");
            e.printStackTrace();
        }
        // Output

        try {
            WritableWorkbook wb_out = Workbook.createWorkbook(out_file);
            for (String str:
                route_map.keySet()) {
                TorpoRoute route = route_map.get(str);
                WritableSheet sheet = wb_out.createSheet(route.GetRouteLabel(), 0);
                route.PrintOutRouteMsg(sheet);
            }
            wb_out.write();
            wb_out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TorpoRoute GetRouteByName(String name) {
        if (this.route_map.containsKey(name)) {
            return route_map.get(name);
        } else {
            return null;
        }
    }

    public ArrayList<TorpoRoute> getRoutesByDevName(String devName) {
        if (this.torpoDeviceMap.containsKey(devName)) {
            return this.torpoDeviceMap.get(devName);
        } else {
            return new ArrayList<>();
        }
    }
}
