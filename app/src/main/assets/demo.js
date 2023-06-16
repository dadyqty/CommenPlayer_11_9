import Map from './ol/Map.js';
import TileLayer from './ol/layer/Tile.js';
import View from './ol/View.js';
import XYZ from './ol/source/XYZ.js';

var center = [121.9132384, 38.5340249]
var tempright = 0;
var tempup = 0;
const map = new Map({
  target: 'map',
  layers: [],
  view: new View({
    center: center, // 地图中心
    projection: 'EPSG:4326', // 对应地图的切片规则
    zoom: 8, // 地图初始化渲染等级
    minZoom: 7, // 地图最小缩放等级
    maxZoom: 9, // 地图最大缩放等级
    rotation: 0
  }),
});

var layer = new TileLayer({
  source: new XYZ({
    url: "MapTiles/{z}/{x}/{y}.png", // 加载虚拟目录下的图片
    wrapX: true,
  })
});

var zoomIn = document.querySelector('#zoom-in');
zoomIn.addEventListener('click', function () {
  var view = map.getView(); // 获取view
  var zoom = view.getZoom(); // 获取zoom
  view.setZoom(zoom + 1); // 修改zoom
});

var zoomOut = document.querySelector('#zoom-out');
zoomOut.addEventListener('click', function () {
  var view = map.getView(); // 获取view
  var zoom = view.getZoom(); // 获取zoom
  view.setZoom(zoom - 1); // 修改zoom
});

var pantoright = document.querySelector('#pantoright');
pantoright.addEventListener('click', function () {
  var view = map.getView(); // 获取view地图视图
  var zoom = view.getZoom(); // 获取zoom
  tempright++;
  view.setCenter([center[0]+tempright/zoom,center[1]+tempup/zoom]); // setCenter平移地图
});

var pantoleft = document.querySelector('#pantoleft');
pantoleft.addEventListener('click', function () {
  var view = map.getView(); // 获取view地图视图
  var zoom = view.getZoom(); // 获取zoom
  tempright--;
  view.setCenter([center[0]+tempright/zoom,center[1]+tempup/zoom]); // setCenter平移地图
});


var pantoup = document.querySelector('#pantoup');
pantoup.addEventListener('click', function () {
  var view = map.getView(); // 获取view地图视图
  var zoom = view.getZoom(); // 获取zoom
  tempup++;
  view.setCenter([center[0]+tempright/zoom,center[1]+tempup/zoom]); // setCenter平移地图
});


var pantodown = document.querySelector('#pantodown');
pantodown.addEventListener('click', function () {
  var view = map.getView(); // 获取view地图视图
  var zoom = view.getZoom(); // 获取zoom
  tempup--;
  view.setCenter([center[0]+tempright/zoom,center[1]+tempup/zoom]); // setCenter平移地图
});

var restore = document.querySelector('#restore');
restore.addEventListener('click', function () {
  var view = map.getView(); // 获取view地图视图
  tempright = 0;
  tempup = 0;
  view.setCenter(center); // setCenter平移地图
});

map.addLayer(layer);

