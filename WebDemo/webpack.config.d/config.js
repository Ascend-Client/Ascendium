//https://gist.github.com/hikaMaeng/2b38fb3d0bb23e0d9dbccc2f4168af28
const EsbuildLoader = require("esbuild-loader");
config.module.rules.push({
    test: /\.js$/, // 모든 JS 파일을 처리
    loader: "esbuild-loader",
    options: {
        target: "es2015", // ES2015 이상의 모듈만 처리
        loader: "js", // JavaScript 처리
    },
});

config.module.rules.push({
    test: /\.ts$/, // 모든 TypeScript 파일을 처리
    loader: "esbuild-loader",
    options: {
        target: "es2015", // ES2015 이상의 모듈만 처리
        loader: "ts", // TypeScript 처리
    },
});

config.optimization = {
    minimize: true, // 코드 압축
    minimizer: [
        new EsbuildLoader.EsbuildPlugin({
            target: "es2015",
        }),
    ],
};
console.log("Using ESBuild for bundling");