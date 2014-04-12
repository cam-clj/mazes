goog.addDependency("base.js", ['goog'], []);
goog.addDependency("../cljs/core.js", ['cljs.core'], ['goog.string', 'goog.array', 'goog.object', 'goog.string.StringBuffer']);
goog.addDependency("../cam_clj/mazes/common.js", ['cam_clj.mazes.common'], ['cljs.core']);
goog.addDependency("../cam_clj/mazes/generate/drunken_walk.js", ['cam_clj.mazes.generate.drunken_walk'], ['cljs.core', 'cam_clj.mazes.common']);
goog.addDependency("../monet/core.js", ['monet.core'], ['cljs.core']);
goog.addDependency("../monet/canvas.js", ['monet.canvas'], ['cljs.core', 'monet.core']);
goog.addDependency("../cam_clj/mazes/draw.js", ['cam_clj.mazes.draw'], ['cljs.core', 'cam_clj.mazes.common', 'monet.canvas']);
goog.addDependency("../cam_clj/mazes/core.js", ['cam_clj.mazes.core'], ['cljs.core', 'cam_clj.mazes.generate.drunken_walk', 'cam_clj.mazes.common', 'cam_clj.mazes.draw']);
goog.addDependency("../cam_clj/mazes/graph.js", ['cam_clj.mazes.graph'], ['cljs.core']);