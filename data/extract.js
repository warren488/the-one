const fs = require('fs')
const filePath = './epsg27700/nottingham/'
const fileName = 'road_line.csv'
// const file = filePath + fileName;

function CSVtoWKT(filePath, fileName) {
const file = filePath + fileName;
  fs.readFile(file, 'utf8', (err, data) => {
    if (err) {
      console.error(err)
      return
    }
    let file = data;
    let lines = file.split('\n');
    let headings = lines.shift().split(',');
    const tracks = [];
    const roads = [];
    const tracksCSV = [headings.join(',')];
    const roadsCSV = [headings.join(',')];

    console.log(headings.indexOf("highway"))
    for (let i = 0; i < lines.length; i++) {
      // the first item has many commas in it so we need to separate by using quotes first
      const splitLine = lines[i].split('"')
      // the rest are separated by commas
      const restofline = splitLine[2]?.split(',');
      // first element will be an empty space
      if (restofline && restofline[1] === "track") {
        tracks.push(splitLine[1]);
        tracksCSV.push(lines[i])
      } else {
        roads.push(splitLine[1]);
        roadsCSV.push(lines[i])
      }
    }
    // lines.unshift(headings)
    if (roads.length > 0) {
      fs.writeFile(filePath + 'roads.wkt', roads.join('\n'), err => {
        if (err) {
          console.error(err)
          return
        }
        //file written successfully
      })
    }
    if (tracks.length > 0) {
      fs.writeFile(filePath + 'tracks.wkt', tracks.join('\n'), err => {
        if (err) {
          console.error(err)
          return
        }
        //file written successfully
      })
    }
    // this section below splits the csv to 2 separate csv files for roads and tracks
    // if (roadsCSV.length > 1) {
    //   fs.writeFile(filePath + 'roads.csv', roadsCSV.join('\n'), err => {
    //     if (err) {
    //       console.error(err)
    //       return
    //     }
    //     //file written successfully
    //   })
    // }
    // if (tracksCSV.length > 1) {
    //   fs.writeFile(filePath + 'tracks.csv', tracksCSV.join('\n'), err => {
    //     if (err) {
    //       console.error(err)
    //       return
    //     }
    //     //file written successfully
    //   })
    // }
  });

}

function WKTtoCSV(pathName, fileName) {
  fs.readFile(pathName + fileName, 'utf8', (err, data) => {
    if (err) {
      console.error(err)
      return
    }
    const CSVLines = ["WKT,"];

    let file = data;

    let lines = file.split('\r\n');
    console.log(lines);
    for (const line of lines) {
      if (line.length > 0) {
        CSVLines.push(`"${line}",`);
      }
    }
    if (CSVLines.length > 1) {
      fs.writeFile(pathName + fileName + '.csv', CSVLines.join('\n'), err => {
        if (err) {
          console.error(err)
          return
        }
        //file written successfully
      })
    }
  });
}

CSVtoWKT(filePath, fileName)